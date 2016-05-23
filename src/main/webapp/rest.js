var Rest = {

    context: function(path) {
        var p = document.location.pathname.replace(/[^/]*$/, '') + 'rest' path;
        return p;
    },

    addMessage : function( message, callback ) {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', this.context('/messages'), true);
        xhr.setRequestHeader("content-type", "application/json");
        xhr.onload = function() {
            callback.call(this);
        };
        xhr.send(JSON.stringify(message));
    },

    retrieveMessages : function( callback ) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', this.context('/messages'), true);
        console.log(this.context);
        xhr.onload = function() {
            var messages = JSON.parse(this.responseText);
            callback.call(xhr, messages);
        };
        xhr.send();
    }
};