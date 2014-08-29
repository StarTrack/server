/** @jsx React.DOM */
var Home = React.createClass({
  getInitialState:function(){
      return {
        //login : 0 do not know, 1 not logged, 2 logged
        login:0
      };
  },
  render:function(){
    return <div className="yoers">
      <a className="fat-button" href="/auth">LOG ME WITH SPOTIFY</a>
    </div>
  } 
});
