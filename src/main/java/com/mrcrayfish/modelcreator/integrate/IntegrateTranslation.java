package com.mrcrayfish.modelcreator.integrate;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.block.Resources;
import com.mrcrayfish.modelcreator.util.Util;

public class IntegrateTranslation extends Integrator
{
	private Set<Map.Entry<String, List<String>>> translations;
	private Iterator<Map.Entry<String, List<String>>> translationItr;
	Map.Entry<String, List<String>> currentEntry; //cant get current entry fro iterator without moving it

	@Override
	public String generate() {
		if(translationItr == null)
			generateTranslations();
		
		String languageName = Resources.languages.stream().filter(l -> l.key.equals(currentEntry.getKey())).map(v -> v.name).collect(Collectors.toList()).get(0);
		String out = languageName + ":\n";
		out += currentEntry.getValue().stream().collect(Collectors.joining(",\n", "", ""));
		
		return out;
	}
	
	private void generateTranslations() {
		Map<String, List<String>> translationsMap = new HashMap<>();
		
		BlockManager.translation.getAllTranslations().forEach((key, value) -> {
			List<String> items = new ArrayList<>();
			String name = constructJsonTranslation("block." + IntegrateDialog.modid + "." + IntegrateDialog.assetName, value.name);
			items.add(name);
			if(value.tooltip != null) {
				String tooltip = constructJsonTranslation("block." + IntegrateDialog.modid + "." + IntegrateDialog.assetName + ".tooltip", value.tooltip);
				items.add(tooltip);
			}
			translationsMap.put(key, items);
		});
		translations = translationsMap.entrySet();
		translationItr = translations.iterator();
		currentEntry = translationItr.next();
	}
	
	private String constructJsonTranslation(String key, String value) {
		return "\"" + key + "\": \"" + value + "\"";
	}

	@Override
	public void integrate() {
		if(this.content.equals("All translations integrated!"))
			return;
		
		String outData = currentEntry.getValue().stream().collect(Collectors.joining(",\n\t", "\t", ""));
		
		Path path = getAssetFolder().resolve("lang").resolve(currentEntry.getKey() + ".json");
		if(path.toFile().exists()) {
			StringSelection stringSelection = new StringSelection(outData);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}else {
			try {
				writeToFile(path, "{\n" + outData + "\n}");
			} catch (IOException e) {
				Util.writeCrashLog(e);
			}
		}
		startTextEditor(path);
		if(translationItr.hasNext()) {
			currentEntry = translationItr.next();
			this.content = generate();
		}else {
			this.content = "All translations integrated!";
		}
	}

}
