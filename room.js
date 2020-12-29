class room {
    constructor(codice, player1, connection1) {
        this.codice = codice;
        this.player1 = player1;
        this.connection1 = connection1;
    }

    setPlayer2(player2, connection2) {
        this.player2 = player2;
        this.connection2 = connection2;
    }

    getCode() {
        return this.codice;
    }

    getCon1(){
        return this.connection1
    }

    getCon2(){
        return this.connection2
    }

}
exports.room = room;
