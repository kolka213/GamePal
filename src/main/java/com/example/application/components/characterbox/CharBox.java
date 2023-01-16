package com.example.application.components.characterbox;

import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import lombok.Setter;

public class CharBox extends VerticalLayout {

    @Setter
    @Getter
    private Character character;

    public CharBox(Character character) {
        this.character = character;
        initComponents();
        setStyle();
    }

    private void initComponents() {
        Label characterAsText = new Label(String.valueOf(character));
        characterAsText.getElement().getStyle().set("font-size", "var(--lumo-font-size-xxxl)");
        add(characterAsText, new Hr());
    }

    private void setStyle(){
        setAlignItems(Alignment.CENTER);
        getStyle().set("border", "1px solid lightgrey");
        getStyle().set("border-radius", "var(--lumo-border-radius)");
        getStyle().set("box-shadow", "0 2px 4px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)");
    }
}
