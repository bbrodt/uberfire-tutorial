package com.mycompany.uftutorial.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.UberFirePreferences;
import org.uberfire.workbench.events.UberFireEvent;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;

@EntryPoint
public class ClientEntryPoint {

  /**
   * Gets invoked early in the startup sequence, as soon as all this bean's
   * {@code @Inject'ed} fields are initialized. Errai Bus and UberFire services
   * are not yet available.
   */
  @PostConstruct
  private void earlyInit() {
    UberFirePreferences.setProperty("org.uberfire.client.workbench.widgets.listbar.context.disable", true);
  }

  /**
   * Gets invoked late in the startup sequence, when all UberFire framework
   * bootstrapping has completed.
   */
  @AfterInitialization
  private void finalInit() {
    hideLoadingPopup();
  }

  /**
   * Fades out the "Loading application" pop-up which was included in the host
   * page by UberFireServlet.
   */
  private void hideLoadingPopup() {
      final Element e = RootPanel.get( "loading" ).getElement();

      new Animation() {

          @Override
          protected void onUpdate( double progress ) {
              e.getStyle().setOpacity( 1.0 - progress );
          }

          @Override
          protected void onComplete() {
              e.getStyle().setVisibility( Style.Visibility.HIDDEN );
          }
      }.run( 500 );
  }

  private void eventSnooper(@Observes UberFireEvent anyEvent) {
    GWT.log(anyEvent.toString());
  }
}
