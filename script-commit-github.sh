#!/bin/bash

#####################################################################################################################################################
#  DESCRIPTION: It updates the current GitHub repository with the last changes. 
#  USAGE: ./script-commit-github.sh [options]
#  OPTIONS:
#    -h   Display help message
#  NOTES:
#    - This script requires git to be installed.
#  AUTHOR: Kaizten Analytics S.L. (info@kaizten.com)
#####################################################################################################################################################

main () {
  echo "# Pulling repository:"
  git pull
  if [ $? -eq 0 ]; then
    git add .
    set +e
    if [ -n "$(git status --porcelain)" ]; then
      read -p "# Enter your message in the commit: " message
      set -e
      git commit -m "${message}"
      echo "# Pushing data to remote GitHub repository:"
      git push
      echo "# Done!"
    else
      set -e
      echo "# Nothing to update"
    fi
  else
    echo "# ERROR. Conflicts must be solved."
    exit
  fi
}

main "$@"; exit