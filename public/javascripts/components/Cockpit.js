/** @jsx React.DOM */
var Cockpit = React.createClass({
  getInitialState:function(){
      return {
        //login : 0 do not know, 1 not logged, 2 logged
        login:0
      };
  },
  render:function(){
    return <section className="yoers">
	<div className="yoer10">IF 
	<input type="text" placeholder="Yo username"/>
	 Yoes FIP 
         then save to playlist&nbsp;
         <span className="playlist">Toto</span>
	</div>
    </section>;
  }
});
