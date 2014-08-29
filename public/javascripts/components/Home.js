/** @jsx React.DOM */
var Home = React.createClass({
  render:function(){
    return 	<div>
    <section className="section green">
    	<div className="container">
    		<p>Fip Radio live playing tracks right in your Spotify playlist, in one tap!</p>
    	</div>
    </section>
    <section className="section spotigreen">
      	<div className="container">
      		<a className="fat-button" href="/auth">LOG ME WITH SPOTIFY</a>
      	</div>
    </section>
    </div>
  }
});
