package team.unstudio.udpl.command.anno;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import team.unstudio.udpl.command.CommandResult;

public class CommandWrapper {
	
	private final String node;
	private final AnnoCommandManager manager;
	private final List<CommandWrapper> children = new ArrayList<>();
	private final CommandWrapper parent;
	
	private Object obj;
	
	private Method command;
	private Method tabcomplete;
	
	private String permission;
	private Class<? extends CommandSender>[] senders;
	private String usage;
	private String description;
	private boolean allowOp;
	
	private boolean hasStringArray;
	
	private Class<?>[] requireds;
	private Class<?>[] optionals;
	private List<List<String>> requiredCompletes;
	private String[] requiredUsages;
	private String[] optionalUsages;
	private List<List<String>> optionalCompletes;
	private Object[] optionalDefaults;

	public CommandWrapper(String node,AnnoCommandManager manager,CommandWrapper parent) {
		this.node = node.toLowerCase();
		this.manager = manager;
		this.parent = parent;
	}
	
	public String getNode() {
		return node;
	}

	public List<CommandWrapper> getChildren() {
		return children;
	}
	
	public Object getObj() {
		return obj;
	}
	
	public String getPermission() {
		return permission;
	}

	public Class<? extends CommandSender>[] getSenders() {
		return senders;
	}

	public String getUsage() {
		return usage;
	}

	public String getDescription() {
		return description;
	}

	public boolean isAllowOp() {
		return allowOp;
	}
	
	public Method getMethod() {
		return command;
	}
	
	@Nullable
	public CommandWrapper getParent() {
		return parent;
	}
	
	public AnnoCommandManager getCommandManager() {
		return manager;
	}
	
	public String[] getRequiredUsages() {
		return requiredUsages;
	}

	public String[] getOptionalUsages() {
		return optionalUsages;
	}
	
	public CommandResult onCommand(CommandSender sender,org.bukkit.command.Command command,String label,String[] args) {
		if (!checkSender(sender))
			return CommandResult.WrongSender;

		if (!checkPermission(sender))
			return CommandResult.NoPermission;

		// 检查参数数量
		if (requireds.length > args.length)
			return CommandResult.NoEnoughParameter;

		// 转换参数
		Object[] objs = new Object[this.command.getParameterTypes().length];
		objs[0] = sender;
		{
			List<Integer> errorParameterIndexsList = Lists.newArrayList();

			for (int i = 0, parameterLength = this.command.getParameterTypes().length
					- (hasStringArray ? 2 : 1); i < parameterLength; i++) {
				if (i < args.length)
					try{
						if (i < requireds.length)
							objs[i + 1] = transformParameter(requireds[i], args[i]);
						else
							objs[i + 1] = transformParameter(optionals[i - requireds.length], args[i]);
					}catch(Exception e){
						errorParameterIndexsList.add(i);
					}
				else
					objs[i + 1] = optionalDefaults[i - args.length];
			}

			int[] errorParameterIndexs = new int[errorParameterIndexsList.size()];
			for(int i=0,size = errorParameterIndexsList.size() ; i < size;i++)
				errorParameterIndexs[i] = errorParameterIndexsList.get(i);
			
			if (errorParameterIndexs.length!=0){
				getCommandManager().onErrorParameter(sender, command, label, args, this, errorParameterIndexs);
				return CommandResult.ErrorParameter;
			}
		}

		if (hasStringArray){
			if(requireds.length + optionals.length < args.length)
				objs[objs.length - 1] = Arrays.copyOfRange(args, requireds.length + optionals.length, args.length);
			else 
				objs[objs.length - 1] = new String[0];
		}
		

		// 执行指令
		try {
			if (this.command.getReturnType().equals(boolean.class))
				return (boolean) this.command.invoke(obj, objs) ? CommandResult.Success : CommandResult.Failure;
			else {
				this.command.invoke(obj, objs);
				return CommandResult.Success;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CommandResult.Failure;
		}
	}
	
	public boolean checkSender(CommandSender sender){
		if(getSenders() == null)
			return false;
		
		for (Class<? extends CommandSender> s : getSenders())
			if (s.isAssignableFrom(sender.getClass()))
				return true;
		
		return false;
	}
	
	public boolean checkPermission(CommandSender sender){
		if (isAllowOp()&&sender.isOp())
			return true;
		
		if(getPermission() == null || getPermission().isEmpty())
			return true;
		
		if (sender.hasPermission(getPermission())) 
			return true;

		return false;
	}
	
	@SuppressWarnings("deprecation")
	protected Object transformParameter(Class<?> clazz,String value){
		if(value == null || value.isEmpty())
			return null;
		else if (clazz.equals(String.class))
			return value;
		else if (clazz.equals(int.class) || clazz.equals(Integer.class))
			return Integer.parseInt(value);
		else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class))
			return Boolean.parseBoolean(value);
		else if (clazz.equals(float.class) || clazz.equals(Float.class))
			return Float.parseFloat(value);
		else if (clazz.equals(double.class) || clazz.equals(Double.class))
			return Double.parseDouble(value);
		else if (clazz.equals(long.class) || clazz.equals(Long.class))
			return Long.parseLong(value);
		else if (clazz.equals(byte.class) || clazz.equals(Byte.class))
			return Byte.parseByte(value);
		else if (clazz.equals(short.class) || clazz.equals(Short.class))
			return Short.parseShort(value);
		else if (clazz.equals(Player.class))
			return Bukkit.getPlayerExact(value);
		else if (clazz.equals(OfflinePlayer.class))
			return Bukkit.getOfflinePlayer(value);
		else if (clazz.equals(Material.class))
			return Material.valueOf(value);
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> onTabComplete(String[] args){
		if(command==null){
			if(args.length<=requireds.length)
				return requiredCompletes.get(args.length-1);
			else if(args.length<=optionals.length)
				return optionalCompletes.get(args.length-requireds.length-1);
			return Collections.EMPTY_LIST;
		}else{
			try {
				return (List<String>) tabcomplete.invoke(obj, args);
			} catch (Exception e) {
				return Collections.EMPTY_LIST;
			}
		}
	}
	
	public void setMethod(Object obj,Method method){
		this.obj = obj;
		this.command = method;
		method.setAccessible(true);
		Command anno = method.getAnnotation(Command.class);
		permission = anno.permission();
		senders = anno.senders();
		usage = anno.usage();
		description = anno.description();
		allowOp = anno.allowOp();
		
		Class<?>[] parameterTypes = method.getParameterTypes();
		hasStringArray = parameterTypes[parameterTypes.length-1].equals(String[].class);
		
		//参数载入
		List<Class<?>> requireds = new ArrayList<>();
		List<Class<?>> optionals = new ArrayList<>();
		List<String> requiredUsages = new ArrayList<>();
		List<String> optionalUsages = new ArrayList<>();
		requiredCompletes = new ArrayList<>();
		optionalCompletes = new ArrayList<>();
		List<Object> optionalDefaults = new ArrayList<>();
		
		Parameter[] parameters = method.getParameters();
		for(int i=0;i<parameters.length;i++){
			{
				Required annoRequired = parameters[i].getAnnotation(Required.class);
				if(annoRequired!=null){
					requireds.add(parameters[i].getType());
					requiredUsages.add(annoRequired.usage() == null || annoRequired.usage().isEmpty()
							? parameters[i].getName() : annoRequired.usage());
					requiredCompletes.add(Collections.unmodifiableList(Arrays.asList(annoRequired.complete())));
					continue;
				}
			}
			
			{
				Optional annoOptional = parameters[i].getAnnotation(Optional.class);
				if(annoOptional!=null){
					optionals.add(parameters[i].getType());
					optionalUsages.add(annoOptional.usage() == null || annoOptional.usage().isEmpty()
							? parameters[i].getName() : annoOptional.usage());
					optionalDefaults.add(transformParameter(parameters[i].getType(), annoOptional.value()));
					optionalCompletes.add(Collections.unmodifiableList(Arrays.asList(annoOptional.complete())));
					continue;
				}
			}
		}
		this.requireds = requireds.toArray(new Class<?>[requireds.size()]);
		this.optionals = optionals.toArray(new Class<?>[optionals.size()]);
		this.requiredUsages = requiredUsages.toArray(new String[requiredUsages.size()]);
		this.optionalUsages = optionalDefaults.toArray(new String[optionalUsages.size()]);
		this.optionalDefaults = optionalDefaults.toArray(new String[0]);
		
		//自动补全载入
		for(Method m:obj.getClass().getDeclaredMethods()){
			TabComplete tab = m.getAnnotation(TabComplete.class);
			if(tab == null)
				continue;
			
			if(Arrays.equals(tab.value(), anno.value())){
				m.setAccessible(true);
				tabcomplete = m;
				break;
			}
		}
	}
}
