var Socket = {
    
    socket : null,

    context: function(path) {
        return 'ws://' + document.location.host + document.location.pathname.replace(/[^/]*$/, '') + path;
    },

    subscribe : function(context, onmessageCallback) {
        this.socket = new WebSocket(this.context(context));
        this.socket.onopen = function() {
            console.log('socket has been opened');
        };
        this.socket.onmessage = function(event) {
            var message = JSON.parse(event.data);
            onmessageCallback.call(this, message);
        };
    }
};