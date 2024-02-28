package com.rs.game.system.scripts;

import java.io.File;
import java.util.Arrays;

/**
 * A class used to manage the loading of scripts.
 * @author 'Vexia
 * 
 */
public class ScriptManager {

	/**
	 * The directory to load scripts from.
	 */
	private static final String DIRECTORY = "scripts/dialogue";

	/**
	 * The amount of scripts loaded.
	 */
	public static int amount;

	/**
	 * Runs the script and returns the current script context after executing.
	 * @param context The script to run.
	 * @param args The arguments.
	 * @return The last script context executed.
	 */
	public static ScriptContext run(ScriptContext context, Object...args) {
		ScriptContext ctx = context;
		do {
			ctx = ctx.run(args);
		}
		while (ctx != null && ctx.isInstant());
		return ctx;
	}
	
	/**
	 * Runs the script and returns the current script context after executing.
	 * @param context The script to run.
	 * @param name The method name.
	 * @return The method script context.
	 */
	public static ScriptContext getMethod(ScriptContext context, String name) {
		ScriptContext ctx = context;
		while (ctx != null) {
			if (ctx.getCondition() != null) {
				context = getMethod(ctx.getCondition(), name);
				if (context != null) {
					return context;
				}
			}
			if (ctx.getName().equals(name)) {
				System.out.println(ctx + ", " + Arrays.toString(ctx.getParameters()));
				return ctx;
			}
			ctx = ctx.getInstruction();
		}
		return null;
	}
	
	/**
	 * Initiates the chain reaction of script loading.
	 */
	public static void load() {
		amount = 0;
		load(new File(DIRECTORY));
		System.out.println("Parsed " + amount + " open633 script" + (amount == 1 ? "": "s") + "...");
	}

	/**
	 * Loads scripts from a directory.
	 * @param directory the directory. 
	 * @throws Throwable the throwable.
	 */
	private static void load(final File directory)  {
		try {
			for (File file : directory.listFiles()) {
				if (file.getName().equals(".DS_Store")) {
					continue;
				}
				if (file.isDirectory()) {
					load(file);
					continue;
				}
				ScriptContext context = ScriptCompiler.parseRaw(file);
				if (ScriptCompiler.getBuilder() != null) {
					ScriptCompiler.getBuilder().configureScript(context);
				}
				amount++;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("Error loading at directory - " + directory + "!");
		}
	}


}
