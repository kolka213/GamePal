package com.example.application.components.characterbox;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import lombok.Setter;

public class CharBox extends VerticalLayout {

    @Setter
    @Getter
    private Character character;

    private final Label characterAsText = new Label();

    public CharBox(Character character) {
        this.character = character;
        initComponents();
        setStyle();
    }

    private void initComponents() {
        this.characterAsText.setText(String.valueOf(character));
        this.characterAsText.setVisible(false);
        this.characterAsText.getElement().getStyle().set("font-size", "var(--lumo-font-size-xxxl)");
        add(this.characterAsText);
    }

    private void setStyle(){
        setAlignItems(Alignment.CENTER);
        setMinHeight(150f, Unit.PIXELS);
        getStyle().set("border", "1px solid lightgrey");
        getStyle().set("border-radius", "var(--lumo-border-radius)");
        getStyle().set("box-shadow", "0 2px 4px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)");
    }

    public void setVisible(boolean visible){
        this.characterAsText.setVisible(visible);
    }

    public boolean isVisible(){
        return this.characterAsText.isVisible();
    }
}
