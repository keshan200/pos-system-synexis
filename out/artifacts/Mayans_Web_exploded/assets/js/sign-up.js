async function signUp() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password
    };

    const userJson = JSON.stringify(user);

    const response = await fetch(
        "SignUp",
        {
            method: "POST",
            body: userJson,

            header: {
                "Content-Type": "application/json"
            }
        });

    if (response.ok) {// success
        const  json = await  response.json();
//        console.log(json);
        if (json.status) {  //if true
            window.location = "verify-account.html";
        } else {//when status false
            // custome message
//            console.log(json.message);
            document.getElementById("message").innerHTML=json.message;

        }
    } else {
        document.getElementById("message").innerHTML="Registration failed. Please try again";

    }
}
/*async function signUp() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password
    };

    const userJson = JSON.stringify(user);

    try {
        const response = await fetch("SignUp", {
            method: "POST",
            body: userJson,
            headers: {   // fix: should be 'headers', not 'header'
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            const json = await response.json();

            if (json.status) {  // Registration success
                // now fetch user info to check role
                const userResponse = await fetch("GetLoggedInUser");
                const userData = await userResponse.json();

                if (userData.status && userData.role === "ADMIN") {
                    // If ADMIN, redirect to admin page
                    window.location.href = "./admin.html";

                } else {
                    // If normal user, redirect to user dashboard or homepage
                    window.location = "index.html";
                }

            } else {
                // Show custom message if registration failed
                document.getElementById("message").innerHTML = json.message;
            }

        } else {
            document.getElementById("message").innerHTML = "Registration failed. Please try again";
        }
    } catch (err) {
        console.error(err);
        document.getElementById("message").innerHTML = "Server error. Please try again later.";
    }
}*/
