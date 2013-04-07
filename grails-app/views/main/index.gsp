<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
	<title>  Welcome to our Cloud</title>
	<style type="text/css">
	#main{
	text-align: center;
	margin-top: 20px;
	margin-bottom: 20px;
	border-style: groove;
	}
	
	
	</style>
</head>

<body>
<div class="title"> 
<h1 align="center" ><font size="18"> Cloud Access </font></h1>
</div>

<div id="main">
<form name="form1" method="post" action="">

<label >Select Template</label>

 
<select name="temp" id="template">
<option  value="griffons_peppermint_3_20120722_desktop">PepperMint</option>
<option  value="griffons_linuxmint_14.1_cinnamon_desktop">Mint</option>
<option  value="griffons_ubuntu_12.04_desktop">Ubuntu</option>
</select>
 
<br/><br/>
<label>CPU Core</label>
<select name="cpu" >
<option value="1">1 </option>
<option value="2">2</option>
<option value="4">4</option>
</select> 
<br/><br/>
<label>Memory</label>
<select name="memory">
<option value="1024">1024</option>
<option value="2048">2048</option>
<option value="4096">4096</option>
</select> 
<br/><br/>
<label>Virtual Machine Name</label>
<input name="vmname"> </input>
<br/><br/>
<input type="submit" name= "event" value="CreateVm"></button>
</form>
</div>






</body>

</html>