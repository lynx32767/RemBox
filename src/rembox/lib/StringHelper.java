package rembox.lib;

public class StringHelper {

    public static String appendPath(String first, String toAppend) {
        if(!first.equals("Root")) {
            if(first.endsWith("\\")) {
                return first + toAppend;
            }
            return first + "\\" + toAppend;
        }
        return toAppend;
    }

    public static String pathToName(String path) {
        if(!path.equals("Root")) {
            String[] splitPath = path.split("\\\\");
            if(splitPath.length > 0) {
                return splitPath[splitPath.length - 1];
            }
            return path;
        }
        return "Root";
    }

    public static String parentFile(String path) {
        String[] paths = path.split("\\\\");
        if(path.equals("Root") || paths.length <= 1) {
            return "Root";
        }

        String end = paths[0];
        for(int i = 1; i < paths.length - 1; i++) {
            end = appendPath(end, paths[i]);
        }
        if(end.equals(paths[0])) {
            end += "\\";
        }
        return end;
    }

}
