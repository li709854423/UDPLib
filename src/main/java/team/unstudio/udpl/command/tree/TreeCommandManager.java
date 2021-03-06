package team.unstudio.udpl.command.tree;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 树形指令管理者
 */
public class TreeCommandManager implements CommandExecutor,TabCompleter{

	private final JavaPlugin plugin;
	private final String name;
	private final List<CommandNode> children = new ArrayList<>();
	
	private String noPermissionMessage = "";
	private String noEnoughParameterMessage = "";
	private String wrongSenderMessage = "";
	private String errorParameterMessage = "";
	private String unknownCommandMessage = "";
	private String runCommandFailureMessage = "";
	
	/**
	 * 创建指令管理者
	 * @param name 指令
	 * @param plugin 插件
	 */
	public TreeCommandManager(String name,JavaPlugin plugin){
		this.name = name;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		int i=0;
		List<CommandNode> tsubs = children;
		while(!tsubs.isEmpty()&&i<args.length){
			for(CommandNode s:tsubs){
				if(s.getNode().equalsIgnoreCase(args[i])){
					i++;
					tsubs = s.getChildren();
					if(tsubs.isEmpty()){
						String targs[] = new String[args.length-i];
						for(int j=0;j<targs.length;j++)targs[j]=args[i+j];
						switch (s.onCommand(sender, targs)) {
						case ErrorParameter:
							onErrorParameter(sender, command, label, args, s);
							break;
						case NoEnoughParameter:
							onNoEnoughParameter(sender, command, label, args, s);
							break;
						case NoPermission:
							onNoPermission(sender, command, label, args, s);
							break;
						case WrongSender:
							onWrongSender(sender, command, label, args, s);
							break;
						case Failure:
							onRunCommandFailure(sender, command, label, args, s);
							break;
						case Success:
							break;
						default:
							break;
						};
						return true;
					}
					break;
				}
			}
		}
		onUnknownCommand(sender, command, label, args);
		return true;
	}

	/**
	 * 添加指令子指令
	 * @param node
	 * @return
	 */
	public TreeCommandManager addNode(CommandNode node){
		children.add(node);
		return this;
	}

	public String getNoPermissionMessage() {
		return noPermissionMessage;
	}

	public TreeCommandManager setNoPermissionMessage(String def) {
		this.noPermissionMessage = def;
		return this;
	}

	public String getNoEnoughParameterMessage() {
		return noEnoughParameterMessage;
	}

	public TreeCommandManager setNoEnoughParameterMessage(String def) {
		this.noEnoughParameterMessage = def;
		return this;
	}

	public String getWrongSenderMessage() {
		return wrongSenderMessage;
	}

	public TreeCommandManager setWrongSenderMessage(String def) {
		this.wrongSenderMessage = def;
		return this;
	}
	
	public String getErrorParameterMessage() {
		return errorParameterMessage;
	}

	public TreeCommandManager setErrorParameterMessage(String def) {
		this.errorParameterMessage = def;
		return this;
	}

	protected void onNoPermission(CommandSender sender, Command command, String label, String[] args, CommandNode handler){
		sender.sendMessage(noPermissionMessage);
	}
	
	protected void onNoEnoughParameter(CommandSender sender, Command command, String label, String[] args, CommandNode handler){
		sender.sendMessage(noEnoughParameterMessage);
	}
	
	protected void onWrongSender(CommandSender sender, Command command, String label, String[] args, CommandNode handler){
		sender.sendMessage(wrongSenderMessage);
	}
	
	protected void onErrorParameter(CommandSender sender, Command command, String label, String[] args, CommandNode handler){
		sender.sendMessage(errorParameterMessage);
	}
	
	protected void onUnknownCommand(CommandSender sender, Command command, String label, String[] args){
		sender.sendMessage(unknownCommandMessage);
	}
	
	protected void onRunCommandFailure(CommandSender sender, Command command, String label, String[] args, CommandNode handler){
		sender.sendMessage(runCommandFailureMessage);
	}
	
	public String getUnknownCommandMessage() {
		return unknownCommandMessage;
	}

	public TreeCommandManager setUnknownCommandMessage(String def) {
		this.unknownCommandMessage = def;
		return this;
	}

	public String getRunCommandFailureMessage() {
		return runCommandFailureMessage;
	}

	public TreeCommandManager setRunCommandFailureMessage(String def) {
		this.runCommandFailureMessage = def;
		return this;
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * 注册指令
	 */
	public TreeCommandManager registerCommand(){
		plugin.getCommand(name).setExecutor(this);
		plugin.getCommand(name).setTabCompleter(this);
		return this;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<>();
		
		int i=0;
		List<CommandNode> tsubs = children;
		while(!tsubs.isEmpty()&&i<args.length-1){
			for(CommandNode s:tsubs){
				if(s.getNode().equalsIgnoreCase(args[i])){
					i++;
					tsubs = s.getChildren();
					if(tsubs.isEmpty()){
						String targs[] = new String[args.length-i];
						for(int j=0;j<targs.length;j++)targs[j]=args[j+i];
						list.addAll(s.onTabComplete(args));
					}
					break;
				}
			}
		}
		if(!tsubs.isEmpty())for(CommandNode s:tsubs)if(args[i].isEmpty()||s.getNode().startsWith(args[args.length-1]))list.add(s.getNode());
		
		if(list.isEmpty())for(Player player:Bukkit.getOnlinePlayers())if(player.getName().startsWith(args[0]))list.add(player.getName());
		return list;
	}
}
