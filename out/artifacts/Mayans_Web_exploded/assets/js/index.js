function indexOnloadFunctions() {
    checkSessionCart();
    loadProductData();
}
async function checkSessionCart() {
    const popup = new Notification();
    const response = await fetch("CheckSessionCart");
    if (!response.ok) {
        popup.error({
            message: "Something went wrong! Try again shortly"
        });
    }
}

async function loadProductData() {

    const popup = new Notification();
    const response = await fetch("LoadHomeData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            loadBrands(json);
            loadNewArrivals(json);
        } else {
            popup.error({
                message: "Something went wrong! Try again shortly"
            });
        }
    } else {
        popup.error({
            message: "Something went wrong! Try again shortly"
        });
    }
}

async function loadCategories() {
    try {
        const response = await fetch("LoadCategories");
        if (!response.ok) {
            throw new Error("Failed to load categories");
        }

        const categories = await response.json();
        const dropdown = document.getElementById("categoriesDropdownMenu");

        dropdown.innerHTML = ""; // clear old content

        categories.forEach(cat => {
            const item = document.createElement("a");
            item.className = "dropdown-item";
            item.href = `shop.html?category=${encodeURIComponent(cat.name)}`;
            item.innerHTML = `<i class="fa fa-leaf text-success mr-2"></i> ${cat.name}`;
            dropdown.appendChild(item);
        });

    } catch (error) {
        console.error("Error loading categories:", error);
    }
}


function loadBrands(json) {
    const product_brand_container = document.getElementById("product-brand-container");
    let product_brand_card = document.getElementById("product-brand-card");
    product_brand_container.innerHTML = "";
    let card_delay = 200;
    json.brandList.forEach(item => {
        let product_brand_card_clone = product_brand_card.cloneNode(true);
        product_brand_card_clone.querySelector("#product-brand-mini-card")
            .setAttribute("data-sal", "zoom-out");
        product_brand_card_clone.querySelector("#product-brand-mini-card")
            .setAttribute("data-sal-delay", String(card_delay));
        product_brand_card_clone.querySelector("#product-brand-a")
            .href = "search.html";
        product_brand_card_clone.querySelector("#product-brand-title")
            .innerHTML = item.name;
        product_brand_container.appendChild(product_brand_card_clone);
        card_delay += 100;
        sal();
    });
}

function loadNewArrivals(json) {
    const new_arrival_product_container = document.getElementById("new-arrival-product-container");
    new_arrival_product_container.innerHTML = "";

    json.productList.forEach(item => {
        let product_card =
            ` <div class="item">
                                <div class="card card-product">
                                    <div class="card-ribbon">
                                        <div class="card-ribbon-container right">
                                            <span class="ribbon ribbon-primary">SPECIAL</span>
                                        </div>
                                    </div>
                                    <div class="card-badge">
                                        <div class="card-badge-container left">
                                            <span class="badge badge-default">
                                                Until 2018
                                            </span>
                                            <span class="badge badge-primary">
                                                20% OFF
                                            </span>
                                        </div>
                                        <img id="ms-product-image-1" src="product-images\\${item.id}\\image1.png" alt="Card image 2" class="card-img-top">
                                    </div>
                                    <div class="card-body">
                                        <h4 class="card-title">
                                            <a href="single-product.html?id=${item.id}">${item.title}
                                        </h4>
                                        <div class="card-price">
                                            <!-- <span class="discount">Rp. 300.000</span> -->
                                            <span class="reguler">Rs. ${new Intl.NumberFormat(
                "en-US",
                { minimumFractionDigits: 2 })
                .format(item.price)}</span>
                                        </div>
                                        <a onclick="addToCart(${item.id},1);" class="btn btn-block btn-primary">
                                            Add to Cart
                                        </a>

                                    </div>
                                </div>
                            </div>  `;
        new_arrival_product_container.innerHTML += product_card;
    });
}

async function addToCart(productId, qty) {
    const popup = new Notification();// link notification js in single-product.html
    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);
    if (response.ok) {
        const json = await response.json(); // await response.text();
        if (json.status) {
            popup.success({
                message: json.message
            });
        } else {
            popup.error({
                message: "Something went wrong. Try again"
            });

        }
    } else {
        popup.error({
            message: "Something went wrong. Try again"
        });
    }
}
