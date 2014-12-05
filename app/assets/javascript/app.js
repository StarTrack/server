$(document).ready(function() {
    var songs = [{
        title: 'Pastime Paradise',
        meta: 'Ray Barretto - La Cuna'
    },{
        title: 'Wicked Game',
        meta: 'Chris Isaak - No Begining No End'
    },{
        title: 'Le Dicen Dolor',
        meta: 'Bio Ritmo - Puerta del Sur'
    }];

    $('.yo .iphone li.fip').click(function() {
        var $fip = $(this);
        $fip.addClass('loading');
        delay(1000).then(function() {
            var $yoSent = $fip.find('.yo-sent');
            $fip.addClass('done');
            $yoSent.addClass('done');
            return delay(1500).then(function() {
                $yoSent.removeClass('done');
            });
        }).then(function() {
            var song = songs.shift();
            if(song) {
                var $title = '<span class="title">$title</span>'.replace(/\$title/g, song.title);
                var $meta = '<span class="meta">$meta</title>'.replace(/\$meta/g, song.meta);
                var $song = '<li>' + $title + $meta + '</li>';
                $('.playlist .iphone ul').append($song);
            }
        }).always(function() {
            $fip.removeClass('loading').removeClass('done');
        });
    });

    function delay(ms) {
        var d = $.Deferred();
        window.setTimeout(function() {
            d.resolve();
        }, ms);
        return d.promise();
    }
});