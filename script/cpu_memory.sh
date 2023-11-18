#!/bin/bash

COLLECTOR_SERVICE=http://localhost:8090?alias=cpuMemory

function get_value {
  echo $1 | sed 's/, /,/g' | cut -d"," -f $2 | cut -d" " -f 1
}

function calc_memory {
  echo "$1*1024" | bc | awk '{printf("%d\n",$1)}'
}

function detect_info {
  local info=`top -bn 1 | awk 'NR==3||NR==4{print $0}'`
  local cpu=`echo "$info" | awk 'NR==1{print $0}' | awk -F':' '{print $2}'`
  local memory=`echo "$info" | awk 'NR==2{print $0}' | awk -F':' '{print $2}'`
  echo $cpu
  local us=`get_value "$cpu" 1`
  local sy=`get_value "$cpu" 2`
  local id=`get_value "$cpu" 4`
  local total=`get_value "$memory" 1`
  local free=`get_value "$memory" 2`
  local used=`get_value "$memory" 3`
  local buff=`get_value "$memory" 4`
  # echo `calc_memory $total` "-" `calc_memory $free` "-" `calc_memory $used` "-" `calc_memory $buff`
  # echo $us"-"$sy"-"$id
  send_huron `calc_memory "$total"` `calc_memory "$free"` `calc_memory "$used"` `calc_memory "$buff"` "$us" "$sy" "$id"
}

function send_huron {
  local time=`date +%s`"000"
  local data='{"uniqueId": "abe", "total": "'$1'", "free": "'$2'", "used": "'$3'", "buff": "'$4'", "us": "'$5'", "sy": "'$6'", "id": "'$7'", "time": '$time'}'
  local req='{"extractorId":"li01", "realtimeList": ['$data']}'
  echo $req
  local result=$(curl --max-time 10 -sL -w "%{http_code}" -X POST $COLLECTOR_SERVICE \
  -H "Content-Type: application/json" \
  -H "Connection: keep-alive" \
  -H "Keep-Alive: timeout=5, max=100" \
  -d "$req" \
  -o /dev/null)

  if [ $result -ne 200 ];
  then
     echo "$now: 发送告警信息失败"
  else
     echo "$now: 发送告警信息成功"
  fi
}

# detect_info

while [ true ];
 do
  detect_info
  /bin/sleep 1
 done;
