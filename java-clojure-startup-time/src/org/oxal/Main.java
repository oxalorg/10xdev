package org.oxal;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import java.lang.management.ManagementFactory;

class Main {

    public static void main(String[] args) {
        long currentTime = System.currentTimeMillis();
        // long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
	var processStartTime = ProcessHandle.current().info().startInstant().get();

        // long beforeClojureLoad = System.currentTimeMillis();

	// IFn require = Clojure.var("clojure.core", "require");
        // long afterClojureLoad = System.currentTimeMillis();
        // System.out.println("Clojure loaded at: " + afterClojureLoad);

	// require.invoke(Clojure.read("org.oxal.startup"));
	// IFn nameFn = Clojure.var("org.oxal.startup", "name");
        // long afterClojureInvoke = System.currentTimeMillis();

	// Object result = nameFn.invoke();
	// System.out.println(result);
        System.out.println("Elapsed time for JVM startup: " + (currentTime - processStartTime.toEpochMilli()) + " ms");
        // System.out.println("Elapsed time for JVM startup: " + (currentTime - jvmStartTime) + " ms");
        // System.out.println("Elapsed time to load Clojure: " + (afterClojureLoad - beforeClojureLoad) + " ms");
        // System.out.println("Elapsed time to invoke Clojure function: " + (afterClojureInvoke - afterClojureLoad) + " ms");
        // System.out.println("Total time (JVM + Clojure): " + (afterClojureInvoke - jvmStartTime) + " ms");
    }
}
