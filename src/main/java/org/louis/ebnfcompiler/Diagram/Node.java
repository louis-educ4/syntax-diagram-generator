package org.louis.ebnfcompiler.Diagram;

public class Node {
    private boolean isRuleId;
    private boolean isTerminal;
    private boolean isHidden;
    private String name;
    private String textContent;

    public  Node(boolean isRuleId, boolean isTerminal, boolean isHidden, String name, String textContent) {
        this.isRuleId = isRuleId;
        this.isTerminal = isTerminal;
        this.isHidden = isHidden;
        this.name = name;
        this.textContent = textContent;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setIsRuleId(boolean isRuleId) {
        this.isRuleId = isRuleId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsRuleId() {
        return this.isRuleId;
    }
}
