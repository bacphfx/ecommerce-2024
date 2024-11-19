package com.ecommerce.admin.setting;

import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.common.Constants;
import com.ecommerce.common.entity.Currency;
import com.ecommerce.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class SettingController {
    @Autowired
    private SettingService settingService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model){
        List<Setting> settings = settingService.listAllSettings();
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("currencies", currencies);

        settings.forEach(setting -> {
            model.addAttribute(setting.getKey(), setting.getValue());
        });

        model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);

        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam(name = "imageUpload")MultipartFile multipartFile,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag settingBag = settingService.getGeneralSetting();
        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);

        updateSettingValuesFromForm(request, settingBag.getListSettings());

        redirectAttributes.addFlashAttribute("message", "General settings have been saved");

        return "redirect:/settings";
    }

    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);
            String uploadDir = "../site-logo/";

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag){
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> optionalCurrency = currencyRepository.findById(currencyId);
        if (optionalCurrency.isPresent()){
            Currency currency = optionalCurrency.get();
            String symbol = currency.getSymbol();
            settingBag.updateCurrencySymbol(symbol);
        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> settings){
        for (Setting setting : settings){
            String value = request.getParameter(setting.getKey());
            if (value != null){
                setting.setValue(value);
            }
        }
        settingService.saveAll(settings);

    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request,
                                         RedirectAttributes redirectAttributes){
        List<Setting> mailServerSettings = settingService.getMailServerSettings();
        updateSettingValuesFromForm(request, mailServerSettings);

        redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved");

        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplatesSettings(HttpServletRequest request,
                                         RedirectAttributes redirectAttributes){
        List<Setting> mailTemplatesSettings = settingService.getMailTemplatesSettings();
        updateSettingValuesFromForm(request, mailTemplatesSettings);

        redirectAttributes.addFlashAttribute("message", "Mail templates settings have been saved");

        return "redirect:/settings";
    }

    @PostMapping("/settings/save_payment")
    public String savePaymentSettings(HttpServletRequest request,
                                            RedirectAttributes redirectAttributes){
        List<Setting> paymentSettings = settingService.getPaymentSettings();
        updateSettingValuesFromForm(request, paymentSettings);

        redirectAttributes.addFlashAttribute("message", "Payment settings have been saved");

        return "redirect:/settings";
    }
}
