# Hangman

**This game is purposed only for linux distributions**

## Install:
If java is not already already installed execute the following commands to install the JVM:
```
# Install apt package
sudo apt install default-jre
# Check if installed
java -version
```

In a terminal, change your working directory to the repo and execute the following commands:
```
chmod +x install.sh
./install.sh
```

Or you can manually create the ~/.hangman directory if it doesn't already exist, move the hangman.jar file in there and update your shell script configuration file (e.g. ~/.bashrc or ~/.zshrc) with the following line:
```
alias hangman="java --module-path\
 ~/.hangman/javafx-sdk/lib\
 --add-modules javafx.controls,javafx.fxml\
 -jar ~/.hangman/hangman.jar"
```
and run:
```
source ~/.bashrc
```

## Starting the game:
After installing you will be able to run the game from a terminal using the following command:
```
hangman
```

## Uninstall:
Change your working directory in the repo again and execute the following commands:
```
chmod +x uninstall.sh
./uninstall.sh
```

## Description:
This project was written in eclipse. The whole eclipse project can be found in **hangman.zip** with just a couple differences in order to store files in eclipse workspace and not be relied on certain OS.

The project uses **java-json.jar** and **openjfx-17.0.1_linux-x64_bin-sdk**.

**LibraryID** can be retrieved from the following link:
https://openlibrary.org
by clicking on a book, copying its Library field from ID Numbers section in the description and appending
.json in the end. There is an example of a LibraryID in the LibraryID_example.txt file.

**DictionaryID** can be whatever you want. It is the ID of the file that will be created to store the dictionary.
