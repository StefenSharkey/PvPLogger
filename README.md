PvPLogger Readme
================

PvPLogger is a plugin for Bukkit that logs all damage events that involve any of the following:

* Player attacking or killing a player.
* Player attacking or killing a mob.
* Player being attacked or killed by a mob, entity, block, etc.

PvPLogger will be ported over to [Sponge](http://www.spongepowered.org/) when the Sponge API allows for more entity interactions.

Compiling
---------

PvPLogger compiles against Java 1.7 and also uses Maven.

* [Maven](http://maven.apache.org/download.cgi)
* [Oracle JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html)

From PvPLogger's root directory, executing the following command will compile PvPLogger:

`mvn clean package`

Contributing
------------

If you would like to submit your code to this project, I'd be more than happy to consider adding it! I have only a few guidelines I ask for you to follow:

* **Use Google Code Style with 120 column limit.** Disregard the column limit if it makes the code unreadable.
 * [Code Style Documentation](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html)
 * [Eclipse Code Style XML](https://code.google.com/p/google-styleguide/source/browse/trunk/eclipse-java-google-style.xml)
 * [IntelliJ Code Style XML](https://code.google.com/p/google-styleguide/source/browse/trunk/intellij-java-google-style.xml)
* **Test your code.** Please don't submit buggy code.