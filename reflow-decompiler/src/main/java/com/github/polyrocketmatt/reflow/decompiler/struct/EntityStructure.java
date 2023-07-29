package com.github.polyrocketmatt.reflow.decompiler.struct;

import com.github.polyrocketmatt.reflow.decompiler.wrapper.EntityWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EntityStructure<T extends EntityWrapper> {

    private final HashMap<String, ArrayList<String>> pointerTable;
    private final HashMap<String, T> classTable;

    public EntityStructure(List<T> wrappers) {
        this.pointerTable = new HashMap<>();
        this.classTable = new HashMap<>();

        try {
            buildStructure(wrappers);
        } catch (RuntimeException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void buildStructure(List<T> wrappers) throws RuntimeException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> buildPointerTable(wrappers));
        executor.submit(() -> buildClassTable(wrappers));
        executor.shutdown();

        if (!executor.awaitTermination(5, TimeUnit.MINUTES))
            throw new RuntimeException("Failed to build entry structure in time");
    }

    private void buildPointerTable(List<T> wrappers) {
        List<String> names = wrappers.stream().map(EntityWrapper::getName).toList();
        List<String[]> paths = new ArrayList<>(names.stream().map(name -> name.split("/")).toList());

        for (String[] path : paths) {
            StringBuilder buildPath = new StringBuilder();

            int pathLength = path.length - 1;
            for (int pathIndex = 0; pathIndex < pathLength; pathIndex++) {
                String key = path[pathIndex + 1];
                buildPath.append(path[pathIndex]).append("/");

                //  If the key is not in the pointer table, add it
                List<String> children = pointerTable.getOrDefault(buildPath.toString(), new ArrayList<>());
                if (!children.contains(key))
                    children.add(key);
                pointerTable.put(buildPath.toString(), new ArrayList<>(children));
            }
        }
    }

    private void buildClassTable(List<T> wrappers) {
        for (T wrapper : wrappers) {
            String name = wrapper.getName();
            String[] splitName = name.split("/");
            String simpleName = splitName[splitName.length - 1];

            classTable.put(simpleName, wrapper);
        }
    }

    public List<String> getAbsoluteParents() {
        return this.pointerTable.keySet().stream().filter(key -> key.split("/").length == 1).toList();
    }

    public boolean isClass(String name) {
        return classTable.containsKey(name);
    }

    public boolean isPackage(String path) {
        return pointerTable.keySet().stream().anyMatch(pathKey -> pathKey.equals(path));
    }

    public List<String> getChildren(String name) {
        return pointerTable.getOrDefault(name, new ArrayList<>());
    }

    public T getClassWrapper(String name) {
        return classTable.get(name);
    }

}
