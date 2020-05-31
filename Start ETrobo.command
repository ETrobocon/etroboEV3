#!/bin/bash
echo
echo "------------"
echo " jtBeerHall - an implementation of Homebrew sandbox"
echo "------------"
echo " as 'Start ETrobo.command' Ver 4.31a.200531"
# Copyright (c) 2020 jtLab, Hokkaido Information University
# by TANAHASHI, Jiro(aka jtFuruhata) <jt@do-johodai.ac.jp>
# Released under the MIT license
# https://opensource.org/licenses/mit-license.php
#

if [ "$1" = "clean" ]; then
    if [ -z "$BEERHALL" ]; then
        BEERHALL="$(cd "$(dirname "$0")"; pwd)/BeerHall"
    fi
    sudo rm /etc/bashrc_BeerHall
    sudo rm /etc/bashrc_vscode
    sudo rm -rf "$BEERHALL"
    exit 0
fi

if [ -z "$BEERHALL" ]; then
    echo
    echo "try to create a new BeerHall"

    if [ -z "$1" ]; then
        hallName="BeerHall"
    else
        hallName="$1"
    fi

    pwd="$(cd "$(dirname "$0")"; pwd)"

    if [ -e "$pwd/$hallName" ]; then
        echo "'$pwd/$hallName' already exists. please delete it or use other name or"
        read -p "Overwrite? (y/N): " yn
        case "$yn" in
            [yY]*) rm -rf "$pwd/$hallName";;
            *)     exit 1;;
        esac
    fi

    profile="$HOME/.bash_profile"
    touch "$profile"
    echo
    echo "add \$BEERHALL env var to $profile" 
    export BEERHALL="$pwd/$hallName"
    if [ -z "`cat $profile 2>&1 | grep BEERHALL`" ]; then
        echo "envvar named 'BEERHALL' is added into $profile"
        echo "# ----- this section was added by jtBeerHall -----" >> $profile
        echo "export BEERHALL=\"$BEERHALL\"" >> $profile
        echo "# ------------------------------------------------" >> $profile
    fi

    bashrc="/etc/bashrc_BeerHall"
    echo "add $bashrc"
    echo 'if [ -z "$BEERHALL_INVOKER" ]; then' | sudo tee $bashrc
    echo '    . "$BEERHALL/BeerHall"' | sudo tee -a $bashrc
    echo 'else' | sudo tee -a $bashrc
    echo '    . "$BEERHALL/BeerHall" setpath' | sudo tee -a $bashrc
    echo 'fi' | sudo tee -a $bashrc

    bashrc="/etc/bashrc_vscode"
    sudo touch $bashrc
    echo "add $bashrc"
    if [ -z "`cat $bashrc 2>&1 | grep BEERHALL`" ]; then
        echo "'BEERHALL_INVOKER' event is added into $bashrc"
        echo '# ----- this section was added by jtBeerHall -----' | sudo tee -a $bashrc
        echo 'if [ "$BEERHALL_INVOKER" = "ready" ]; then' | sudo tee -a $bashrc
        echo '    . "$BEERHALL/BeerHall" setpath' | sudo tee -a $bashrc
        echo 'fi' | sudo tee -a $bashrc
        echo '# ------------------------------------------------' | sudo tee -a $bashrc
    fi

    echo "make symbolic link"
    mkdir -p "$BEERHALL/usr/local"
    cd "$BEERHALL"
    ln -s "$HOME/.gitconfig" .gitconfig
    ln -s "$HOME/.ssh" .ssh
    ln -s "$HOME/.vscode" .vscode
    ln -s "$HOME/Applications" Applications
    ln -s "$HOME/Library" Library

    echo "install HomeBrew, please wait a *few hours*"
    cd "$BEERHALL/usr"
    curl -L https://github.com/Homebrew/brew/tarball/master | tar xz --strip 1 -C local
    export HOMEBREW_CACHE="$BEERHALL/usr/local/cache"
    local/bin/brew install bash bash-completion findutils git wget ruby@2.5 gcc@7 make

    echo "modify gcc7 filenames"
    cd "$BEERHALL/usr/local/bin"
    ls | grep 7$ | while read line; do
        fileName=`echo "$line" | sed -E 's/(.*)-7/\1/'`
        mv "$line" "$fileName"
    done

    echo "make aliases"
    echo "gmake \"\$@\"" > make
    echo "gfind \"\$@\"" > find
    echo "\"/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code\" \"\$@\"" > code
    chmod +x make find code

    echo "make symbolic link from \$BEERHALL/etc to \$BEERHALL/usr/local/etc"
    ln -s "$BEERHALL/usr/local/etc" "$BEERHALL/etc"

    echo "make BeerHall"
    beer=$(mktemp)
    echo 'if [ -z "$BEERHALL_INVOKER" ]; then' > $beer
    echo '    export HOME_ORG="$HOME"' >> $beer
    echo '    export BEERHALL_INVOKER="booting"' >> $beer
    echo 'fi' >> $beer
    echo "export BEERHALL=\"$BEERHALL\"" >> $beer
    echo 'export HOMEBREW_CACHE="$BEERHALL/usr/local/cache"' >> $beer
    echo 'export HOMEBREW_TEMP="/tmp"' >> $beer
    echo 'export RUBY_CONFIGURE_OPTS="--with-openssl-dir=$(brew --prefix openssl@1.1)"' >> $beer
    echo 'export BEERHALL_RUBY="$BEERHALL/usr/local/opt/ruby@2.5/bin"' >> $beer
    echo 'export HOME="$BEERHALL"' >> $beer
    echo 'export SHELL="$BEERHALL/usr/local/bin/bash"' >> $beer
    echo 'export PATH="$BEERHALL:/$BEERHALL/usr/local/bin:$BEERHALL_RUBY:/usr/bin:/bin:/usr/sbin:/sbin"' >> $beer
    echo 'export BEERHALL_PATH="$BEERHALL:/$BEERHALL/usr/local/bin:$BEERHALL_RUBY:/usr/bin:/bin:/usr/sbin:/sbin"' >> $beer
    echo 'export TERM_PROGRAM="BeerHall"' >> $beer
    echo 'export TERM_PROGRAM_VERSION="4.31a"' >> $beer
    echo '' >> $beer
    echo 'if [ "$1" != "setpath" ]; then' >> $beer
    echo '    echo "Welcome, you are in jtBeerHall - an implementation of Homebrew sandbox"' >> $beer
    echo 'fi' >> $beer
    echo '' >> $beer
    echo 'tmpFile=$(mktemp)' >> $beer
    echo "ls -lFa \"\$BEERHALL/etc/profile.d\" | grep -v / | sed -e '1d' | sed -E 's/^.* (.*$)/\\1/' > \$tmpFile" >> $beer
    echo 'while read line; do' >> $beer
    echo '    . "$BEERHALL/etc/profile.d/$line"' >> $beer
    echo 'done < $tmpFile' >> $beer
    echo 'rm $tmpFile' >> $beer
    echo '' >> $beer
    echo 'if [ "$1" != "setpath" ]; then' >> $beer
    echo '    export BEERHALL_INVOKER="ready"' >> $beer
    echo '    cd "$HOME"' >> $beer
    echo '    if [ -n "$1" ]; then' >> $beer
    echo '        bash -l -c "$@"' >> $beer
    echo '    else' >> $beer
    echo '        bash -l' >> $beer
    echo '    fi' >> $beer
    echo 'fi' >> $beer
    mv -f $beer "$BEERHALL/BeerHall"
    chmod +x "$BEERHALL/BeerHall"

    # for startetrobo
    echo "download startetrobo"
    cd "$BEERHALL"
    "$BEERHALL/usr/local/bin/wget" "https://ETrobocon.github.io/etroboEV3/startetrobo"
    chmod +x startetrobo
fi

cd "$BEERHALL"
echo "bash on BeerHall is invoked on `pwd`"
#"$BEERHALL/BeerHall" code .

# for startetrobo
"$BEERHALL/BeerHall" ./startetrobo


