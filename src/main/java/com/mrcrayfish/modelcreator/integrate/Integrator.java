package com.mrcrayfish.modelcreator.integrate;

public abstract class Integrator
{
	protected String content;
	
	public String getContent() {
		return content;
	}
	
	protected String addModid(String text) {
		if(text.contains(":")) {
			return text;
		}
		return IntegrateDialog.modid + ":" + text;
	}
	
	protected String getItemForBlock() {
		if(IntegrateDialog.BlockItem == null) {
			return addModid(IntegrateDialog.assetName);
		}else {
			return addModid(IntegrateDialog.BlockItem);
		}
	}
	
	public abstract void integrate(String text);
}
