/** @jsx React.DOM */
var Cockpit = React.createClass({
  getInitialState:function(){
      return {
        //login : 0 do not know, 1 not logged, 2 logged
        login:0
      };
  },
  render:function(){
    return <h1>Star track</h1>;
  }
});
