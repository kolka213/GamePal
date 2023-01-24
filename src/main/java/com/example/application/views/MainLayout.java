package com.example.application.views;


import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.security.SecurityService;
import com.example.application.views.chat.ChatView;
import com.example.application.views.gamebrowser.GameBrowserView;
import com.example.application.views.masterdetail.CitiesAdminView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private H2 viewTitle;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Collaboration");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        var scrollerLayout = new VerticalLayout(createNavigation(), new Button("Logout", VaadinIcon.SIGN_OUT.create(), buttonClickEvent ->
                securityService.logout()));
        Scroller scroller = new Scroller(scrollerLayout);

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        AppNavItem citiesNavItem = new AppNavItem("Cities", CitiesAdminView.class);
        citiesNavItem.setVisible(securityService.getAuthenticatedUser().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Icon citiesIcon = new Icon(VaadinIcon.PIN);
        citiesIcon.setSize("16px");
        citiesIcon.setColor("var(--lumo-error-color)");

        citiesNavItem.setIcon(citiesIcon);
        citiesNavItem.getElement().appendChild(createAdminBadge());

        AppNavItem wordsNavItem = new AppNavItem("Words", "words");
        wordsNavItem.setVisible(securityService.getAuthenticatedUser().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Icon wordsIcon = new Icon(VaadinIcon.COMMENTS);
        wordsIcon.setSize("16px");
        wordsIcon.setColor("var(--lumo-error-color)");

        wordsNavItem.setIcon(wordsIcon);
        wordsNavItem.getElement().appendChild(createAdminBadge());

        nav.addItem(new AppNavItem("Chat", ChatView.class, "la la-comments"));
        nav.addItem(new AppNavItem("Game Browser", GameBrowserView.class, "la la-columns"));
        nav.addItem(citiesNavItem);
        nav.addItem(wordsNavItem);


        return nav;
    }

    private Element createAdminBadge(){
        Span adminBadge = new Span("ADMIN");
        adminBadge.getElement().getThemeList().addAll(
                Arrays.asList("badge", "error", "primary", "small", "pill"));
        adminBadge.getStyle().set("position", "absolute")
                .set("transform", "translate(20%, 0%)");
        return adminBadge.getElement();
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
