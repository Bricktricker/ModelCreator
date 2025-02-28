package com.mrcrayfish.modelcreator.integrate;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.text.DecimalFormat;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.mrcrayfish.modelcreator.Actions;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.panels.CollisionPanel;

public class IntegrateCode extends Integrator
{
	public static final DecimalFormat FORMAT = new DecimalFormat("#.###");
	
	private int type;
	private CollisionPanel collision;
	
	public IntegrateCode(CollisionPanel collision) {
		this.collision = collision;
	}

	@Override
	public String generate() {
		if(type == 0 && BlockManager.properties.getMaterial() == null) {
			return "No material set!";
		}
		
		switch(type) {
		case 0:
			return genProperties();
		case 1:
			return genCollision();
		default:
			return genBlockItem();
		}
	}

	@Override
	public void integrate() {
		StringSelection stringSelection = new StringSelection(this.content);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
	
	@Override
	public JPanel getAdditionalPanel() {
		JPanel codeSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JComboBox<String> comboBoxType = new JComboBox<>();
		comboBoxType.setPreferredSize(new Dimension(100, 24));
		codeSelectPanel.add(comboBoxType);
		comboBoxType.addItem("Properties");
		comboBoxType.addItem("Collision");
		comboBoxType.addItem("Item");
		comboBoxType.addActionListener(a -> {
			this.type = comboBoxType.getSelectedIndex();
			this.doUpdate();
		});
		
		return codeSelectPanel;
	}
	
	@Override
	public String getButtonText() {
		return "Copy to Clipboard";
	}
	
	private String genProperties() {
		StringBuilder builder = new StringBuilder();
		builder.append("Block.Properties.create(Material.");
		builder.append(BlockManager.properties.getMaterial());
		builder.append(").hardnessAndResistance(");
		builder.append(BlockManager.properties.getHardness());
		builder.append("F, ");
		builder.append(BlockManager.properties.getResistance());
		builder.append("F)");
		
		if(BlockManager.properties.getLightLevel() > 0) {
			builder.append(".lightValue(");
			builder.append(BlockManager.properties.getLightLevel());
			builder.append(")");
		}
		
		if(BlockManager.properties.getSound() != null) {
			builder.append(".sound(SoundType.");
			builder.append(BlockManager.properties.getSound());
			builder.append(")");
		}
		
		return builder.toString();
	}
	
	private String genCollision() {
		StringBuilder builder = new StringBuilder();
		
		//iterate over all rotations
		for(int i = 0; i < 4; i++) {
			builder.append("private static final VoxelShape SHAPE_");
			builder.append(i*90);
			builder.append(" = VoxelShapes.or(");
			String shape = collision.getAllElements().stream().map(this::genVoxelShape).collect(Collectors.joining(", ", "", ");"));
			builder.append(shape);
			builder.append('\n');
			
			collision.getAllElements().forEach(e -> Actions.rotateElement(e, true));
		}
		return builder.toString();
	}
	
	private String genVoxelShape(Element cuboid) {
		StringBuilder builder = new StringBuilder();
		builder.append("Block.makeCuboidShape(");
		
		//startX
		builder.append(FORMAT.format(cuboid.getStartX()));
		builder.append("D, ");
		
		//startY
		builder.append(FORMAT.format(cuboid.getStartY()));
		builder.append("D, ");
		
		//startZ
		builder.append(FORMAT.format(cuboid.getStartZ()));
		builder.append("D, ");
		
		//endX
		builder.append(FORMAT.format(cuboid.getStartX() + cuboid.getWidth()));
		builder.append("D, ");
		
		//endY
		builder.append(FORMAT.format(cuboid.getStartY() + cuboid.getHeight()));
		builder.append("D, ");
		
		//endZ
		builder.append(FORMAT.format(cuboid.getStartZ() + cuboid.getDepth()));
		builder.append("D)");
		
		return builder.toString();
	}
	
	private String genBlockItem() {
		StringBuilder builder = new StringBuilder();
		builder.append("new BlockItem(this, new Item.Properties()");
		
		if(BlockManager.properties.getCreativeTab() != null) {
			builder.append(".group(ItemGroup.");
			builder.append(BlockManager.properties.getCreativeTab());
			builder.append("))");
		}
		
		builder.append(".setRegistryName(\"");
		builder.append(IntegrateDialog.modid);
		builder.append("\", ");
		
		builder.append('"');
		if(IntegrateDialog.BlockItem != null) {
			builder.append(IntegrateDialog.BlockItem);
		}else {
			builder.append(IntegrateDialog.assetName);
		}
		builder.append("\");");
		
		return builder.toString();
	}

}
