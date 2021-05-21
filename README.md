# GEMS
Generic Events Management System.

Created as part of CS3233 Module at NUS High School.

The COVID-19 pandemic has disrupted the way people live their lives. The raging pandemic has triggered unprecedented restrictions not only on the movement of people, but also on a range of economic activities, and the declaration of national emergencies in many countries. To suppress the widespread of COVID-19 in Singapore, our government tightened borders, quarantined suspected cases and ring-fenced any infection clusters through contact tracing. The Ministry of Health (MOH) introduced strict safe distancing measures to limit gatherings outside of work and school. Physical distancing of at least 1m was enforced in most settings where interactions are non-transient. The general public were encouraged to practice good personal hygiene and social responsibility.

In addition, the development of Safe-Entry and TraceTogether apps are national initiatives to combat the spread of the virus by means of identification and tracking of people who have come into contact with COVID-19 carriers. At an organizational level, entities may want to implement their own processes to control and manage visitors to their premises. NUS High organizes several events all year round such as open house, seminars, workshops, competitions, etc. Thus, having a software system to monitor crowds and limit entry to venues would be beneficial to enforce safe distancing measures and contact tracing. I have thus created a Generic Events Management System (GEMS) for NUS High. All visitors to any event will be issued with an entry pass in the form of an AES-Encrypted QR code (eQRCode), containing basic information of the visitor in an encrypted form.


See pdf for all the details.


## Overall User Interface (UI)
GEMS was built on JavaFX and is resizable to a certain extent. The application has a minimized toolbar with tooltips to provide very basic info regarding the tabs. Additionally, the top bar is also custom-made.


## Monitor Tab
The texts shown are much more detailed, with the names of both locations and visitors being factored in. One can simply press scan to process a .png file and pseudo-scan it into the event. Additionally, mass checkout allows everyone to leave at once. This feature is auto-initialized upon closing of the window


## Events, Venues and Ticket Settings Tabs
These tabs remained mostly like the original, although I decided to add a separate tab for venues to avoid a very crowded user interface. Additionally, for the Ticket Settings, I also employed multithreading to ensure that the UI does not lag.


## Trace Tab
Trace and Ticket settings both had authentication to ensure that no unauthorised personnel can access this information. Additonally, since both these tabs required authentication on part of the user, I added a feature such that if you log in to one of the tabs, the other login page will automatically obtain the login details. These will also automatically logout once the window is closed.


## About Tab
I employed animation to rotate the image, a top view of a gem, clockwise. The about pane can also be accessed by pressing on the icon in the top bar of the application.


## Reflection
### Obstacles Faced
I faced many obstacles, mainly because I had not worked on such a large-scale UI before in JavaFX. I didn’t understand many of the features like JAR creation very well and one of the main hardships I faced was that my laptop’s Java Runtime Environment was very outdated, and I had to go through a lot of work to fix this issue.

### Learning Points
I learnt that while JavaFX may be very difficult to create, the overall product is very good. I was pleased by how good a UI it could create with the right amount of dedication. I am now very appreciative towards IntelliJ, which is a great IDE that can auto-generate code very seamlessly.

### Improvements
This application could be made more role-based, i.e. a separate UI for the on-site scanner, admin and tracer. This would improve the overall usefulness of the application because the application will not be too difficult to use. Additionally, as for the task, there should be options to make events multi-day events. This would be useful because this would be a more real-world option – events don’t necessarily just run for a day but can span multiple days. This feature, if employed will thus be much more useful.
