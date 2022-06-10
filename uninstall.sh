#!/bin/bash

if [ -d ~/.hangman ]; then
	rm -r ~/.hangman
fi
[ "$(grep '^alias hangman=' ~/.bash* ~/.profile /etc/profile)" ] && echo -e "$(cat ~/.bashrc | grep -v '^alias hangman')" > ~/.bashrc

[ -f ~/.zshrc ] && [ "$(grep '^alias hangman=' ~/.zshrc)" ] && echo -e "$(cat ~/.zshrc | grep -v '^alias hangman')" > ~/.zshrc