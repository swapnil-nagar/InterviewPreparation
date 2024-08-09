package org.LLD.FileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileFilters {

    public static void main(String args[]) throws IOException {
        List<String> fileNames = getListOfFilesConstant("jh");
        NameFilter nameFilter = new NameFilter("a.log");
        System.out.println(nameFilter.match(getListOfFilesConstant("jh")));
    }

    public List<String> getListOfFiles(String directory) throws IOException {
        Stream<Path> files = Files.walk(Paths.get(directory));
        return files.map(path -> path.getFileName().toString()).collect(Collectors.toUnmodifiableList());
    }

    public static List<String> getListOfFilesConstant(String directory) throws IOException {
        List<String> result = new ArrayList<>();
        result.add("a.log");
        result.add("c.log");

        return result;
    }

    interface IFilter {
        List<String> match(List<String> criteria);
    }

    public static class NameFilter implements IFilter{

        String fileName;

        public NameFilter(String fileName){
            this.fileName = fileName;
        }

        @Override
        public List<String> match(List<String> files) {
            return files.stream().filter(file-> file.equals(fileName)).collect(Collectors.toUnmodifiableList());
        }
    }
}
