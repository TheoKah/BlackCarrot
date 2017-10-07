package com.carrot.blackcarrot.cmd.element;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import com.carrot.blackcarrot.data.BlackData;

public class TypeCmdElement extends PatternMatchingCommandElement
{
	public TypeCmdElement(Text key)
	{
		super(key);
	}
	
	@Override
	protected Iterable<String> getChoices(CommandSource src)
	{
		List<String> list = new ArrayList<>();
		
		for (BlackData.Type type : BlackData.Type.values())
			list.add(type.name());
		
		return list;
	}

	@Override
	protected Object getValue(String choice) throws IllegalArgumentException
	{
		return BlackData.Type.valueOf(choice);
	}
}
