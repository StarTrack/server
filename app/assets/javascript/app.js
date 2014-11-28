$(document).ready(function() {

    var songs = [{
        title: 'Trouble',
        meta: 'Jose James - No Begining No End'
    },{
        title: 'Trouble',
        meta: 'Jose James - No Begining No End'
    },{
        title: 'Trouble',
        meta: 'Jose James - No Begining No End'
    }];

    $('.yo .iphone li').click(function() {
        var song = songs.shift();
        if(song) {
            var $title = '<span class="title">$title</span>'.replace(/\$title/g, song.title);
            var $meta = '<span class="meta">$meta</title>'.replace(/\$meta/g, song.meta);
            var $song = '<li>' + $title + $meta + '</li>';
            console.log($title);
            $('.playlist .iphone ul').append($song);
        }
    });
});