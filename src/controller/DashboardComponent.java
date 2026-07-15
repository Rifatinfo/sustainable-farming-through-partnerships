package controller;

import javafx.scene.Node;

public interface DashboardComponent {

    String getPanelTitle();

    Node buildContent();

    void refresh();
}
