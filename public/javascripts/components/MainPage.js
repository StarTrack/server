/** @jsx React.DOM */
var MainPage = React.createClass({
  getInitialState:function(){
      return {
        //login : 0 do not know, 1 not logged, 2 logged
        login:0,
        user: null
      };
  },
  componentDidMount:function(){
    jQuery.ajax("/users/me").then(this.logOK, this.logKO);
  },
  logOK : function( result ){
    this.setState({
      login:2,
      user: result
    });
  },
  logKO : function(){
    this.setState({
      login:1
    });
  },
  render:function(){
    console.log(this.state);
    var currentPage = <span>loading</span>;
    if(this.state.login === 1) currentPage = <Home/>;
    if(this.state.login === 2) currentPage = <Cockpit user={this.state.user}/>;
    return <div>
      <Header/>
      {currentPage}
      <Startrek/>
    </div>
  }
});
