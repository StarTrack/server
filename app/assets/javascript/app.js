$(document).ready(function() {
    //// DOM

    var $fip = function() {
        return $('.fip');
    };

    var $fipPoint = function() {
        return $('.fip').find('.point');
    };

    var $yoSent = function() {
        return $fip().find('.yo-sent');
    };

    var $yoMask = function() {
        return $('.yo .mask');
    };

    var $playlist = function() {
        return $('.playlist');
    };

    var $playlistMask = function() {
        return $playlist().find('.mask');
    };

    //// DEMO
    function sendYo() {
        return delay(1000).then(function() {
            $fip().addClass('done');
            $yoSent().addClass('done');
        }).then(function() {
            return delay(1500).then(function() {
                $yoSent().removeClass('done');
            });
        });
    }

    function fadeInPlaylist() {
        return delay(1000).then(function() {
            $playlistMask().removeClass('on');
            $playlistMask().addClass('off');
        });
    }

    function fadeOutYo() {
        return delay(10).then(function() {
            $fip().removeClass('loading').removeClass('done');
            $fipPoint().addClass('disabled');
            $yoMask().addClass('on');
            $yoMask().removeClass('off');
        });
    }

    function addToPlaylist() {
        var song = {
            title: 'Pastime Paradise',
            meta: 'Ray Barretto - La Cuna'
        };

        if(song) {
            var $title = '<span class="title">$title</span>'.replace(/\$title/g, song.title);
            var $meta = '<span class="meta">$meta</title>'.replace(/\$meta/g, song.meta);
            var $song = '<li class="loading">' + $title + $meta + '</li>';
            $playlist().find('ul').append($song);
            return delay(1000).then(function() {
                $playlist().find('ul li:last-child').removeClass('loading');
            });
        } else {
            return delay(0);
        }
    }

    //// EVENTS

    $('.yo .iphone li.fip').click(function() {
        $fip().addClass('loading');
        sendYo().then(function() {
            return $.when(fadeInPlaylist(), fadeOutYo());
        }).then(function() {
            return addToPlaylist();
        });
    });

    //// UTILS

    function delay(ms) {
        var d = $.Deferred();
        window.setTimeout(function() {
            d.resolve();
        }, ms);
        return d.promise();
    }
});
