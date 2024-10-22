
console.log("Welcome to Smart Contact Manager");

const toggleSidebars = () => {
    $('.sidebar').toggleClass('hidden');
    
    if ($('.sidebar').hasClass('hidden')) {
        // When hidden, content takes up full width
        $(".content").css("margin-left", "0%");
    } else {
        // When shown, content moves to the right to make space
        $(".content").css("margin-left", "20%");
    }
};
