package com.mrcrayfish.modelcreator.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import com.mrcrayfish.modelcreator.Icons;
import com.mrcrayfish.modelcreator.StateManager;
import com.mrcrayfish.modelcreator.component.JElementList;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementCellEntry;
import com.mrcrayfish.modelcreator.element.ElementCellRenderer;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.ElementManagerState;
import com.mrcrayfish.modelcreator.panels.tabs.ElementPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;

public class CollisionPanel extends JPanel implements ElementManager
{
	private static final long serialVersionUID = 5285529798060998447L;
	
	// Swing Variables
    private SpringLayout layout;
    private DefaultListModel<ElementCellEntry> model = new DefaultListModel<>();
    private JList<ElementCellEntry> list = new JElementList(); //Cubes outliner
    private JScrollPane scrollPane;
    private JPanel btnContainer;
    private ElementPanel elementPanel;
    private JButton btnAdd = new JButton();
    private JButton btnRemove = new JButton();
    private JButton btnDuplicate = new JButton();
    private JTextField name = new JTextField();
    
    public CollisionPanel() {
    	this.setLayout(layout = new SpringLayout());
        this.setPreferredSize(new Dimension(200, 760));
        this.initComponents();
        this.setLayoutConstaints();
    }
    
    private void initComponents() {
    	Font defaultFont = new Font("SansSerif", Font.BOLD, 14);
    	
    	btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
        btnContainer.setPreferredSize(new Dimension(190, 30));

        btnAdd.setIcon(Icons.cube);
        btnAdd.setToolTipText("New Element");
        btnAdd.addActionListener(e -> this.newElement());
        btnAdd.setPreferredSize(new Dimension(30, 30));
        btnContainer.add(btnAdd);

        btnRemove.setIcon(Icons.bin);
        btnRemove.setToolTipText("Remove Element");
        btnRemove.addActionListener(e -> this.deleteElement());
        btnRemove.setPreferredSize(new Dimension(30, 30));
        btnContainer.add(btnRemove);
        
        btnDuplicate.setIcon(Icons.copy);
        btnDuplicate.setToolTipText("Duplicate Element");
        btnDuplicate.addActionListener(e ->
        {
            int selected = list.getSelectedIndex();
            if(selected != -1)
            {
                model.addElement(new ElementCellEntry(new Element(model.getElementAt(selected).getElement())));
                list.setSelectedIndex(model.getSize() - 1);
                StateManager.pushState(this);
            }
        });
        btnDuplicate.setFont(defaultFont);
        btnDuplicate.setPreferredSize(new Dimension(30, 30));
        btnContainer.add(btnDuplicate);
        add(btnContainer);
        
        name.setPreferredSize(new Dimension(190, 30));
        name.setToolTipText("Element Name");
        name.setEnabled(false);
        name.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    updateName();
                }
            }
        });
        name.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                updateName();
            }
        });
        add(name);
        
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(26);
        list.setModel(model);
        list.addListSelectionListener(e ->
        {
            Element selectedElement = getSelectedElement();
            if(selectedElement != null)
            {
            	elementPanel.updateValues(selectedElement);
                name.setEnabled(true);
                name.setText(selectedElement.getName());
                list.ensureIndexIsVisible(list.getSelectedIndex());
            }
        });
        list.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_DELETE)
                {
                    deleteElement();
                    return;
                }
                boolean home = e.getKeyCode() == KeyEvent.VK_HOME;
                if (home || e.getKeyCode() == KeyEvent.VK_END)
                    setSelectedElement(home ? 0 : model.getSize() - 1);
            }
        });
        list.setCellRenderer(new ElementCellRenderer());

        scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(190, 170));
        add(scrollPane);
        
        
        elementPanel = new ElementPanel(this);
        elementPanel.setPreferredSize(new Dimension(190, 500));
        add(elementPanel);
    }
    
    private void setLayoutConstaints()
    {
        layout.putConstraint(SpringLayout.NORTH, name, 212, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.NORTH, btnContainer, 176, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.NORTH, elementPanel, 250, SpringLayout.NORTH, this);
    }

	@Override
	public JList<ElementCellEntry> getList()
	{
		return list;
	}

	@Override
	public Element getSelectedElement()
	{
		int i = list.getSelectedIndex();
        if(model.getSize() > 0 && i >= 0 && i < model.getSize())
        {
            return model.getElementAt(i).getElement();
        }
        return null;
	}

	@Override
	public ElementCellEntry getSelectedElementEntry()
	{
		int i = list.getSelectedIndex();
        if(model.getSize() > 0 && i >= 0 && i < model.getSize())
        {
            return model.getElementAt(i);
        }
        return null;
	}

	@Override
	public void setSelectedElement(int pos)
	{
		if(pos < model.size()) {
            if(pos >= 0) {
                list.setSelectedIndex(pos);
            }else{
                list.clearSelection();
            }
            updateValues();
        }
	}

	@Override
	public List<Element> getAllElements()
	{
		List<Element> list = new ArrayList<>();
        for(int i = 0; i < model.size(); i++) {
            list.add(model.getElementAt(i).getElement());
        }
        return list;
	}

	@Override
	public Element getElement(int index)
	{
		ElementCellEntry elem = model.getElementAt(index);
		return elem != null ? elem.getElement() : null;
	}

	@Override
	public int getElementCount()
	{
		return model.size();
	}

	@Override
	public void updateName()
	{
		String newName = name.getText();
        if(newName.isEmpty())
        {
            newName = "Cuboid";
        }
        Element selectedElement = getSelectedElement();
        if(selectedElement != null)
        {
            selectedElement.setName(newName);
            name.setText(newName);
            list.repaint();
            StateManager.pushState(this);
        }
	}

	@Override
	public void updateValues()
	{
		elementPanel.updateValues(getSelectedElement());
	}

	@Override
	public boolean getAmbientOcc()
	{
		return false;
		//No support for AO for Collision Boxes, ignore it
		//TODO: check if needed in interface
	}

	@Override
	public void setAmbientOcc(boolean occ)
	{
		//No support for AO for Collision Boxes, ignore it
		//TODO: check if needed in interface
	}
	
	@Override
    public void clearElements()
    {
        model.clear();
    }

	@Override
	public void addElement(Element e)
	{
		model.addElement(new ElementCellEntry(e));
	}

	@Override
	public void setParticle(TextureEntry entry)
	{
		//No support for particles for Collision Boxes, ignore it
		//TODO: check if needed in interface
	}

	@Override
	public TextureEntry getParticle()
	{
		return null;
		//No support for particles for Collision Boxes, ignore it
		//TODO: check if needed in interface
	}

	@Override
	public void reset()
	{
		this.clearElements();
	}

	@Override
	public void restoreState(ElementManagerState state)
	{
		this.reset();
        for(Element element : state.getElements())
        {
            this.model.addElement(new ElementCellEntry(new Element(element)));
        }
        this.setSelectedElement(state.getSelectedIndex());
        this.updateValues();
	}

	@Override
	public void newElement()
	{
		model.addElement(new ElementCellEntry(new Element(1, 1, 1)));
        list.setSelectedIndex(model.size() - 1);
        StateManager.pushState(this);
	}

	@Override
	public void deleteElement()
	{
		int selected = list.getSelectedIndex();
        if(selected != -1)
        {
            model.remove(selected);
            name.setText("");
            name.setEnabled(false);
            elementPanel.updateValues(getSelectedElement());
            if(selected >= list.getModel().getSize())
            {
                list.setSelectedIndex(list.getModel().getSize() - 1);
            }
            else
            {
                list.setSelectedIndex(selected);
            }
            StateManager.pushState(this);
        }
	}

}
