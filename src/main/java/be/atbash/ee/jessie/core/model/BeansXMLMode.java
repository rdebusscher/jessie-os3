package be.atbash.ee.jessie.core.model;

public enum BeansXMLMode {
    IMPLICIT("implicit"), ANNOTATED("annotated"), ALL("all");

    private String mode;

    BeansXMLMode(String mode) {

        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static BeansXMLMode getValue(String mode) {
        BeansXMLMode result = null;
        for (BeansXMLMode beansXMLMode : BeansXMLMode.values()) {
            if (beansXMLMode.getMode().equalsIgnoreCase(mode)) {
                result = beansXMLMode;
            }
        }
        return result;
    }

    public static class OptionName {
        public static String name = BeansXMLMode.class.getSimpleName();
    }
}
