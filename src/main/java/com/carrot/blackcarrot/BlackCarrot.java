package com.carrot.blackcarrot;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.carrot.blackcarrot.cmd.BlackActionExecutor;
import com.carrot.blackcarrot.cmd.BlackAddExecutor;
import com.carrot.blackcarrot.cmd.BlackDeleteExecutor;
import com.carrot.blackcarrot.cmd.BlackEditExecutor;
import com.carrot.blackcarrot.cmd.BlackHelpExecutor;
import com.carrot.blackcarrot.cmd.BlackListExecutor;
import com.carrot.blackcarrot.cmd.BlackMainExecutor;
import com.carrot.blackcarrot.cmd.BlackReasonExecutor;
import com.carrot.blackcarrot.cmd.BlackRenameExecutor;
import com.carrot.blackcarrot.cmd.BlackSwapWorldExecutor;
import com.carrot.blackcarrot.cmd.BlackWorldExecutor;
import com.carrot.blackcarrot.cmd.element.ItemCmdElement;
import com.carrot.blackcarrot.cmd.element.TypeCmdElement;
import com.carrot.blackcarrot.data.BlackConfig;
import com.carrot.blackcarrot.data.BlackData;
import com.carrot.blackcarrot.data.BlackLang;
import com.carrot.blackcarrot.listener.BlackEventListener;
import com.google.inject.Inject;

@Plugin(id = "blackcarrot", name = "Black Carrot", authors = {"Carrot"}, url = "https://github.com/TheoKah/BlackCarrot")
public class BlackCarrot {
	private File rootDir;

	private static BlackCarrot plugin;

	@Inject
	private Logger logger;

	@Inject
	@ConfigDir(sharedRoot = true)
	private File defaultConfigDir;

	@Listener
	public void onInit(GameInitializationEvent event) throws IOException
	{
		plugin = this;

		rootDir = new File(defaultConfigDir, "blackcarrot");

		BlackLang.init(rootDir);
		BlackConfig.init(rootDir);
		BlackData.init(rootDir);
	}

	@Listener
	public void onStart(GameStartedServerEvent event)
	{
		BlackLang.load();
		BlackConfig.load();
		BlackData.load();

		CommandSpec cmdHelp = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_HELP))
				.permission("blackcarrot.command.help")
				.executor(new BlackHelpExecutor())
				.build();

		CommandSpec cmdList = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_LIST))
				.permission("blackcarrot.command.list")
				.executor(new BlackListExecutor())
				.build();

		CommandSpec cmdInfo = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_INFO))
				.permission("blackcarrot.command.info")
				.arguments(new ItemCmdElement(Text.of("alias")))
				.executor(new BlackEditExecutor())
				.build();

		CommandSpec cmdAdd = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_ADD))
				.permission("blackcarrot.command.admin")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("alias"))),
						GenericArguments.optional(GenericArguments.remainingJoinedStrings((Text.of("reason")))))
				.executor(new BlackAddExecutor())
				.build();
		
		CommandSpec cmdDel = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_DEL))
				.permission("blackcarrot.command.admin")
				.arguments(new ItemCmdElement(Text.of("item")))
				.executor(new BlackDeleteExecutor())
				.build();
		
		CommandSpec cmdRename = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_RENAME))
				.permission("blackcarrot.command.admin")
				.arguments(new ItemCmdElement(Text.of("item")),
						GenericArguments.string(Text.of("alias")))
				.executor(new BlackRenameExecutor())
				.build();
		
		CommandSpec cmdReason = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_REASON))
				.permission("blackcarrot.command.admin")
				.arguments(new ItemCmdElement(Text.of("item")),
						GenericArguments.remainingJoinedStrings(Text.of("reason")))
				.executor(new BlackReasonExecutor())
				.build();
		
		CommandSpec cmdAction = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_ACTION))
				.permission("blackcarrot.command.admin")
				.arguments(new ItemCmdElement(Text.of("item")),
						new TypeCmdElement(Text.of("type")))
				.executor(new BlackActionExecutor())
				.build();
		
		CommandSpec cmdWorld = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_WORLD))
				.permission("blackcarrot.command.admin")
				.arguments(new ItemCmdElement(Text.of("item")),
						GenericArguments.world(Text.of("world")))
				.executor(new BlackWorldExecutor())
				.build();
		
		CommandSpec cmdSwapWorld = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_SWAPWORLD))
				.permission("blackcarrot.command.admin")
				.arguments(new ItemCmdElement(Text.of("item")))
				.executor(new BlackSwapWorldExecutor())
				.build();

		CommandSpec cmdEdit = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_EDIT))
				.permission("blackcarrot.command.admin")
				.child(cmdRename, "rename")
				.child(cmdReason, "reason")
				.child(cmdWorld, "world")
				.child(cmdAction, "action")
				.child(cmdSwapWorld, "swapworld")
				.executor(new BlackEditExecutor())
				.build();

		CommandSpec cmdMain = CommandSpec.builder()
				.description(Text.of(BlackLang.HELP_MAIN))
				.executor(new BlackMainExecutor())
				.child(cmdHelp, "help", "?", "wiki", "how", "howto", "h")
				.child(cmdList, "list", "l", "show", "view", "see", "display")
				.child(cmdInfo, "info", "i", "why", "more", "ffs", "y", "yudodis", "sad", "sadness", "reason", "explain")
				.child(cmdEdit, "edit", "e", "config", "setup")
				.child(cmdAdd, "add", "a", "append", "disallow", "block", "ban", "b", "forbid", "no", "nope", "nuh", "nah", "meh", "goaway", "bad", "baddog", "banhammer", "non", "nup")
				.child(cmdDel, "del", "d", "remove", "allow", "unblock", "unban", "u", "yes", "yup", "yeah", "si", "oui", "comeback", "good", "gooddog", "goodeboi", "verymuchso")
				.build();

		Sponge.getCommandManager().register(plugin, cmdHelp, "blackcarrothelp", "blackcarrotwiki", "blackhelp", "blackwiki", "blisthelp", "blistwiki");
		Sponge.getCommandManager().register(plugin, cmdMain, "blackcarrot", "blacklist", "bl", "blist");
		

		Sponge.getEventManager().registerListeners(this, new BlackEventListener());
	}
	@Listener
	public void onStop(GameStoppingServerEvent event) {
		BlackConfig.save();
		BlackData.save();
	}

	public static BlackCarrot getInstance()
	{
		return plugin;
	}

	public static Logger getLogger()
	{
		return getInstance().logger;
	}
}
