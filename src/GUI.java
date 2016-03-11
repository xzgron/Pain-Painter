import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by noahtell on 15-05-12.
 */
public class GUI extends JFrame {

    public ImageWorld world;
    private ToolField toolField;
    private InputHandler inputHandler;
    private JScrollPane scrollPane;
    private JList<File> list;
    private JLayeredPane layeredPane;
    private JCheckBox stuckCheckBox;
    public JPanel listPanel;

    public GUI(){
        super();

        setLayout(new BorderLayout());
        Container content = getContentPane();

        toolField = new ToolField(this);
        content.add(toolField, BorderLayout.NORTH);

        //create layer pane under tools
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        content.add(layeredPane, BorderLayout.CENTER);

        //add world to layer pane, with lowest z-ordering
        world = new ImageWorld();
        InputHandler i = new InputHandler();
        layeredPane.add(world, BorderLayout.CENTER, 0);

        //add list panel beside world, add higher z-ordering
        listPanel = new JPanel(new BorderLayout());
        layeredPane.add(listPanel, BorderLayout.EAST, 1);

        //list of imageObjects
        File[] listImageFiles = new File("images").listFiles();

        //Jlist
        list = new JList<File>(listImageFiles);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            //Start holding image
            @Override
            public void mousePressed(MouseEvent e){
                if(world.getImageObjectDropTarget() == null) {
                    int mouseInWorldx = e.getX() - world.getX() + list.getX() + listPanel.getX();
                    int mouseInWorldy = e.getY() - world.getY() + list.getY() + listPanel.getY();
                    int index = list.locationToIndex(e.getPoint());
                    try {
                        world.setImageObjectDropTarget(new ImageObject((File) list.getModel().getElementAt(index)));

                        world.getImageObjectDropTarget().setPosition(world.xScreenToWorld(mouseInWorldx) - (int) (world.getImageObjectDropTarget().getImageCenter().x), world.yScreenToWorld(mouseInWorldy) - (int) (world.getImageObjectDropTarget().getImageCenter().y));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                }
            }

            //render image on under mouse cursor(if possible)
            @Override
            public void mouseDragged(MouseEvent e) {
                 if(world.getImageObjectDropTarget() == null)
                     return;
                int mouseInWorldx = e.getX() - world.getX() + list.getX() + listPanel.getX();
                int mouseInWorldy = e.getY() - world.getY() + list.getY() + listPanel.getY();

                world.getImageObjectDropTarget().getPosition().set(world.xScreenToWorld(mouseInWorldx) - (int) (world.getImageObjectDropTarget().getImageCenter().x), world.yScreenToWorld(mouseInWorldy) - (int) (world.getImageObjectDropTarget().getImageCenter().y));

            }


            //Add image to world if held over it's component(fix)
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                if(world.getImageObjectDropTarget() == null)
                    return;
                int mouseInWorldx = e.getX() - world.getX() + list.getX() + listPanel.getX();
                int mouseInWorldy = e.getY() - world.getY() + list.getY() + listPanel.getY();
                //if mouse ain't in the world
                if(mouseInWorldx < 0 || mouseInWorldy < 0 || mouseInWorldx >= world.getWidth() || mouseInWorldy >= world.getHeight()) {
                    world.setImageObjectDropTarget(null);
                    return;
                }

                world.getImageObjectDropTarget().setPosition(world.xScreenToWorld(mouseInWorldx) - (int) (world.getImageObjectDropTarget().getImageCenter().x), world.yScreenToWorld(mouseInWorldy) - (int) (world.getImageObjectDropTarget().getImageCenter().y));
                world.getImageObjectDropTarget().setStuck(stuckCheckBox.isSelected());

                world.addImageObjectDropTarget(true);
                //Change from Jframe to world coordinates(fix)
            }
        };
        list.addMouseListener(ma);
        list.addMouseMotionListener(ma);

        //How to render induvial cells in list
        list.setCellRenderer(new MyCellRenderer());

        //Create scrollbar for list
        scrollPane = new JScrollPane(list);

        //add scrollbar and list to list panel
        listPanel.add(scrollPane, BorderLayout.NORTH);

        //Add check box for stuck mode
        stuckCheckBox = new JCheckBox("Stuck");
        stuckCheckBox.setVerticalAlignment(SwingConstants.TOP);
        listPanel.add(stuckCheckBox);

        //list panel is invisble until user pushes a button
        listPanel.setVisible(false);

        setBounds(0,0,1000,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        (new Thread(world)).start();
    }
}

class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {

    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.

    public Component getListCellRendererComponent(
            JList<?> list,           // the list
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // does the cell have focus
    {
        //JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        //this.setText(((ImageObject) value).get);
        if(value instanceof File) {
            try {
                Image img = (new ImageIcon(ImageIO.read((File)value))).getImage();
                Image newimg = img.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(newimg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
