package org.flowerplatform.launcher.test;

import org.flowerplatform.launcher.LauncherDto;
import org.flowerplatform.launcher.RunnableWithParam;

public class RunnableHelloWorld implements RunnableWithParam<Void, LauncherDto>{

	@Override
	public Void run(LauncherDto param) {
		System.out.println("hello world from runnable" + param.toString());
		return null;
	}
}
