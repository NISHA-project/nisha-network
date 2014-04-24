#!/bin/bash 

CONFIG=/etc/nisha/nisha-node-manager.properties
echo "Reading config file $CONFIG"
while IFS= read -r line; do
	NAME=`echo $line | awk -F= '{gsub(/[ \t]+/, "", $1); print $1}'`
	VAL=`echo $line | awk -F= '{gsub(/[ \t]+/, "", $2); print $2}'`
	if [ "$NAME" = "couchdb_host" ]; then
		COUCHDBHOST=$VAL
	elif [ "$NAME" = "couchdb_port" ]; then
		COUCHDBPORT=$VAL
	elif [ "$NAME" = "couchdb_nisha_user" ]; then
		NISHA_USR=$VAL
	elif [ "$NAME" = "couchdb_nisha_password" ]; then
		NISHA_PWD=$VAL
	fi 
done < $CONFIG

NAME="Network starter"
USAGE="Invalid usage:\n $NAME [flags]\n"
USAGE="$USAGE""Available flags:\n -h - host [REQUIRED]\n -p - port [REQUIRED]\n"
if [ -z $4 ]; then 
	echo -e $USAGE
elif [ $1 != "-h" ]; then
	echo -e $USAGE
elif [ $3 != "-p" ]; then
	echo -e $USAGE
else
	HOST=$2
	PORT=$4
	echo "NODE host: $HOST port: $PORT COUCHDB host: $COUCHDBHOST port: $COUCHDBPORT"
	if [ -n $HOST ]; then
		if [ -n $PORT ]; then
			if [ -n $COUCHDBHOST ]; then
				if [ -n $COUCHDBPORT ]; then
					echo "nisha network - start...."

					curl -X POST http://$NISHA_USR:$NISHA_PWD@$COUCHDBHOST:$COUCHDBPORT/nisha-node-uris -H 'Content-Type: application/json' -d '{ 
					   "nodeDomainNameFromRingInfo": "'$HOST'",
					   "portNumberFromRingInfo": "'$PORT'",
					   "role": "SUPERNODE",
					   "state": "ACTIVE",
					   "stateReason": "First super node in network"
					}'

					DATE=$(date +'%F %T')
					curl -X POST http://$NISHA_USR:$NISHA_PWD@$COUCHDBHOST:$COUCHDBPORT/nisha-node-uris -H 'Content-Type:application/json' -d '{
					   "lastChangeDate":"'"${DATE}"'",
					   "lastChangeType":"NODE_ADD",
					   "lastChangeNode":"'$HOST'",
					   "networkNodeNames":["'$HOST'"]
					}'
				fi
			fi
		fi
	fi
fi

