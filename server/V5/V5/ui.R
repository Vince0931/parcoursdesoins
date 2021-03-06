navbarPage("Parcours de soins", id="CartoParcours",
           tabPanel("Events",
                    
                    fluidPage(
                      sidebarLayout(
                      sidebarPanel(
                        actionButton(inputId = "addEventTabpanel", 
                                     label = GLOBALaddEventTabpanel
                                     ),
                        actionButton(inputId = "removeEventTabpanel", 
                                     label = GLOBALremoveEventTabpanel),
                        # actionButton(inputId = "ButtonTest", 
                        #              label = "Description"),
                        selectInput(inputId = "eventToRemove",
                                    label = c(""), 
                                    choices = c(""))
                      ),
                      
                      mainPanel(
                        div(id = "mainPanelLinkEvent")
                      )
                      
                      )

                    ),
                    
                    
                    tags$head(
                      includeCSS("www/css/styleLeaflet.css"),
                      includeScript("www/js/newTabpanel.js"),
                      includeScript("www/js/removeId.js"),
                      includeScript("www/js/displayId.js"),
                      includeScript("www/js/goFirstSibling.js"),
                      includeCSS("www/css/ButtonFilter.css"),
                      includeCSS("www/css/Graphics.css")
                    ), # fin tag$head
                    # pour retirer le tabset et le boutton permet de le retirer !
                    
                      tabsetPanel(id = GLOBALeventTabSetPanel,
                                  tabPanel("Context",
                                           div(id="contextId"))
                      )
                    ), ## end tabpanel Event
           
           ### leaflet
           tabPanel("Carte",
                    div(class="outer",
                        leaflet::leafletOutput(GLOBALmapId, width="100%", height="100%"),
                        # panel control pour la sélection
                        absolutePanel(id = GLOBALmapObjectControls, class = "panel panel-default", fixed = TRUE,
                                      draggable = TRUE, top = 60, left = "auto", right = 20, bottom = "auto",
                                      width = 330, height = "auto",
                                      
                                      h4(GLOBALcontrols),
                                      div(id=GLOBALlayerControl)
                        ))
           )
)