const SocketServer = require("websocket").server
const { Socket } = require("dgram")
const http = require("http")
const { room } = require("./room")

const server = http.createServer((req, res) => {})

server.listen(8080, function() {
    console.log("Listening on port 8080...")
})


wsServer = new SocketServer({httpServer:server})

const connections = []

var rooms = {}

wsServer.on("request", function(socket) {
    const connection = socket.accept()
    console.log("Player Connected")

    

    connection.on("message" , (mes) => {

        messaggio = mes.utf8Data
        x = messaggio.split("-")

        if(x[2] == "crea"){      
            room1 = new room(x[0] , x[1] , connection)
            rooms[x[0]] = room1
            console.log(mes.utf8Data)
        } else if (x[2] == "join") {
            if(rooms[x[0]] == null){
                connection.sendUTF("errore-La stanza non esiste");
            } else {
                rooms[x[0]].setPlayer2(x[1] , connection)
                stanza = rooms[x[0]] 
                conn1 = stanza.getCon1()
                conn2 = stanza.getCon2()
                
                setTimeout(() => {  conn1.sendUTF("start-3"); }, 1000);
                setTimeout(() => {  conn2.sendUTF("start-3"); }, 1000);

                setTimeout(() => {  conn1.sendUTF("start-2"); }, 2000);
                setTimeout(() => {  conn2.sendUTF("start-2"); }, 2000);

                setTimeout(() => {  conn1.sendUTF("start-1"); }, 3000);
                setTimeout(() => {  conn2.sendUTF("start-1"); }, 3000);

                setTimeout(() => {  conn1.sendUTF("start-0"); }, 4000);
                setTimeout(() => {  conn2.sendUTF("start-0"); }, 4000);
                    
                
                console.log(mes.utf8Data)
            }

        } else if (x[2] == "endgame"){

            if(connection == conn1){
                conn2.sendUTF("endgame-" + x[1])
            } else if (connection == conn2){
                conn1.sendUTF("endgame-" + x[1])
            }

        
        } else if(x[2] == "dati"){
            stanza = rooms[x[0]] 
            conn1 = stanza.getCon1()
            conn2 = stanza.getCon2()
            if(connection == conn1){
                conn2.sendUTF("dati-" + x[1])
            } else if (connection == conn2){
                conn1.sendUTF("dati-" + x[1])
            }

        
        } else {

        }
        
        
       /* connections.forEach(element => {
            if (element!= connection)
                element.sendUTF(mes.utf8Data)
        }) */
       

    })

    
   
    
    connections.push(connection)

    connection.on("close" , function() {
        console.log("Player Disconnected")
        connections.splice(connections.indexOf(connection) , 1)
        
    })
})