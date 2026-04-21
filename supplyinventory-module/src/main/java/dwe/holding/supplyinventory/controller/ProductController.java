package dwe.holding.supplyinventory.controller;

import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.mapper.ProductMapper;
import dwe.holding.supplyinventory.model.LookupProductCategory;
import dwe.holding.supplyinventory.model.Product;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import dwe.holding.supplyinventory.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/product")
/**
 * This controller uses shared costingSearch via ProdctAndPricingPartialController with PricingController
 */
public class ProductController {
    private final ProductRepository productRepository;
    private final LookupProductCategoryRepository lookupProductCategoryRepository;
    private final ProductMapper productMapper;

    /*** LIST ***/
    @GetMapping("/products")
    String showListPage(Model model, ListForm form) {
        if (form.inputProductId() == null && form.categoryId() == null)
            form = new ListForm(null, null, Boolean.TRUE);
        model
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("salesType", new SalesTypeDummy())
                .addAttribute("productSearchUrl", "/product/search/product")
                .addAttribute("productSearchForm", form)
                .addAttribute("products", List.of())
        ;
        return "supplies-module/product/list";
    }

    /*** SINGLE NEW RECORD ***/
    @GetMapping("/product")
    String newProduct(Model model) {
        Product product = Product.builder()
                .lookupProductCategory(new LookupProductCategory())
                .deleted(YesNoEnum.No)
                .hasBatchNr(YesNoEnum.No)
                .hasSpillage(YesNoEnum.No)
                .deceasedPetPrompt(YesNoEnum.No)
                .taxed(TaxedTypeEnum.SERVICE)
                .build();
        model
                .addAttribute("product", product)
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("taxTypes", TaxedTypeEnum.getWebList())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("salesType", new SalesTypeDummy())
        ;
        return "supplies-module/product/action";
    }

    /*** SINGLE UPDATE RECORD ***/
    @GetMapping("/product/{productId}")
    String initialPag(Model model, @PathVariable Long productId, ListForm productSearchForm) {
        Product product = productId == 0 ? new Product() : productRepository.findById(productId).orElseThrow();
        model
                .addAttribute("product", product)
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
                .addAttribute("taxTypes", TaxedTypeEnum.getWebList())
                .addAttribute("yesNoOptions", YesNoEnum.getWebList())
                .addAttribute("salesType", new SalesTypeDummy())
                .addAttribute("productSearchForm", productSearchForm)
        ;
        return "supplies-module/product/action";
    }

    @PostMapping("/product")
    String saveProduct(Product productForm, RedirectAttributes redirect) {
        // fix boolean coming in
        Product product = new Product();
        if (productForm.getId() != null)
            product = productRepository.findById(productForm.getId()).orElseThrow();

        product = productMapper.fromForm(product, productForm);

        productRepository.save(product);
        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:product/products";
    }


    @DeleteMapping("/product/{productId}/supply/{supplyId}")
    String deleteSupply(@PathVariable Long productId, @PathVariable Long supplyId) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getSupply().getId().equals(supplyId))
            product.setSupply(null);
        productRepository.save(product);
        return "";
    }

    // TODO
    @PostMapping("/product/{productId}/supply/{supplyId}/connect")
    String connectSupply(@PathVariable Long productId, @PathVariable Long supplyId) {
        // set header to close modal
        return "";
    }

    static ListForm getListForm(ListForm form) {
        return form.categoryId() != null ? new ListForm(null, form.categoryId, Boolean.TRUE) : new ListForm(form.inputProductId, null, Boolean.FALSE);
    }

    public record ListForm(Long inputProductId, Long categoryId, Boolean useDropDown) {
    }

    public record SalesTypeDummy() {
        public boolean isPriceInfo() {
            return false;
        }

        public boolean isEstimate() {
            return false;
        }

        public boolean isVisit() {
            return false;
        }

        public boolean isOtc() {
            return false;
        }

        public boolean isCostingProduct() {
            return true;
        }
    }
}