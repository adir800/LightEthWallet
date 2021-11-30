<!doctype html>
<html lang="en">

<head>
<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<!-- Bootstrap CSS -->
<link rel="stylesheet" href="bootstrap.css">

<title>Balance</title>
<style>
.btn-outline-primary {
	display: block;
	margin-left: auto;
	margin-right: auto;
	width: 40%;
}

.table {
	text-align: center;
}

table, th {
	border: 2px solid #DF691A;;
}
</style>
</head>

<body>
	<div class="jumbotron">
		<h1 class="display-3">Balance</h1>
		<br>
		<form name="RegisterForm" method="post" action="GetBalance">
			<table class="table table-hover">
				<thead>
					<tr>
						<th width="50%" class="table-active" scope="col">Address</th>
						<th width="50%" class="table-active" scope="col">${address}</th>
					</tr>
					<tr>
						<th width="50%" class="table-active" scope="col">CurrentBalance</th>
						<th width="50%" class="table-active" scope="col">${balance}</th>
					</tr>
				</thead>
			</table>
		</form>
		<br>
		<div>
			<form action="menu.html">
				<input class="btn btn-outline-primary" type="submit"
					value="Main Menu" />
			</form>
		</div>
	</div>
</body>

</html>