#!/bin/bash
echo "starting download script"
echo "Args to shell:" $*
#
# ARG 1: Path to lsz executable.
# ARG 2: Elf File to download
# ARG 3: TTY port to use.
#
path_to_exe=$1
tty_port_id=$3

echo "Serial Port PORT" $com_port_id 
echo "Using tty Port" $tty_port_id 
echo "NO OF ARGS = $#"

if [ "$#" -eq "3" ]
then
  host_file_name=$2
  echo "download firmware onto target"
  "$path_to_exe/lsz" --escape --binary --overwrite "$path_to_exe/$host_file_name" < $tty_port_id > $tty_port_id
elif [ "$#" -gt "3" ]
then
  echo "execute remote command"
  remote_cmd=$4
  remote_arg1=$5
  remote_arg2=$6
  remote_arg3=$7
  "$path_to_exe/lsz" --escape -v -c "$remote_cmd $remote_arg1 $remote_arg2 $remote_arg3" < $tty_port_id > $tty_port_id
  #ls -al
fi
