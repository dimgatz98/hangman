#!/bin/bash

if ! [ -d ~/.hangman ]; then
	mkdir ~/.hangman
fi
cp hangman.jar ~/.hangman/
# wget https://download2.gluonhq.com/openjfx/17.0.1/openjfx-17.0.1_linux-x64_bin-sdk.zip
# unzip openjfx-17.0.1_linux-x64_bin-sdk.zip
# mv javafx* ~/.hangman/
# rm openjfx-17.0.1_linux-x64_bin-sdk.zip

[ "$(echo $SHELL | grep bash)" ] && [ ! "$(grep '^alias hangman=' ~/.bash* ~/.profile /etc/profile)" ] && echo -e '\nalias hangman="java --module-path ~/.hangman/javafx-sdk-17.0.1/lib --add-modules javafx.controls,javafx.fxml -jar ~/.hangman/hangman.jar"' | tee -a ~/.bashrc && source ~/.bashrc

[ "$(echo $SHELL | grep zsh)" ] && [ ! "$(grep '^alias hangman=' ~/.zshrc)" ] && echo -e '\nalias hangman="java --module-path ~/.hangman/javafx-sdk-17.0.1/lib --add-modules javafx.controls,javafx.fxml -jar ~/.hangman/hangman.jar"' | tee -a ~/.zshrc && source ~/.zshrc
