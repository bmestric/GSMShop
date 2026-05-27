package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.entity.Camera;
import hr.bmestric.gsmshop.entity.Phone;
import hr.bmestric.gsmshop.enums.CameraType;
import hr.bmestric.gsmshop.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

@Controller
@RequestMapping("/compare")
@RequiredArgsConstructor
public class PhoneCompareController {

    private final PhoneRepository phoneRepository;

    @GetMapping
    public String compare(@RequestParam String ids, Model model) {
        List<Phone> phones = phoneRepository.findWithCamerasByIdIn(parseIds(ids));
        if (phones.size() < 2) {
            return "redirect:/products";
        }
        model.addAttribute("phones", phones);
        addCameraCompareData(phones, model);
        addSpecCompareData(phones, model);
        return "compare/compare";
    }

    private void addCameraCompareData(List<Phone> phones, Model model) {
        List<CameraType> cameraTypes = Arrays.stream(CameraType.values())
                .filter(ct -> phones.stream()
                        .anyMatch(p -> p.getCameras().stream().anyMatch(c -> c.getType() == ct)))
                .toList();

        Map<String, Integer> maxMpPerType = new LinkedHashMap<>();
        Map<String, Long> maxMpCountPerType = new LinkedHashMap<>();
        Map<String, Integer> minMpPerType = new LinkedHashMap<>();
        Map<String, Long> minMpCountPerType = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> phoneCameraMpMap = new LinkedHashMap<>();

        for (CameraType ct : cameraTypes) {
            String key = ct.name();
            int maxMp = camMaxMp(phones, ct);
            int minMp = camMinMp(phones, ct);
            maxMpPerType.put(key, maxMp);
            maxMpCountPerType.put(key, countPhonesAtMp(phones, ct, maxMp));
            minMpPerType.put(key, minMp);
            minMpCountPerType.put(key, countPhonesAtMp(phones, ct, minMp));
        }

        for (Phone phone : phones) {
            Map<String, Integer> typeToMp = new LinkedHashMap<>();
            for (CameraType ct : cameraTypes) {
                phone.getCameras().stream()
                        .filter(c -> c.getType() == ct)
                        .mapToInt(Camera::getMegapixels)
                        .max()
                        .ifPresent(mp -> typeToMp.put(ct.name(), mp));
            }
            phoneCameraMpMap.put(String.valueOf(phone.getId()), typeToMp);
        }

        model.addAttribute("cameraTypes", cameraTypes);
        model.addAttribute("maxMpPerType", maxMpPerType);
        model.addAttribute("maxMpCountPerType", maxMpCountPerType);
        model.addAttribute("minMpPerType", minMpPerType);
        model.addAttribute("minMpCountPerType", minMpCountPerType);
        model.addAttribute("phoneCameraMpMap", phoneCameraMpMap);
    }

    private int camMaxMp(List<Phone> phones, CameraType ct) {
        return phones.stream().flatMap(p -> p.getCameras().stream())
                .filter(c -> c.getType() == ct).mapToInt(Camera::getMegapixels).max().orElse(0);
    }

    private int camMinMp(List<Phone> phones, CameraType ct) {
        return phones.stream().flatMap(p -> p.getCameras().stream())
                .filter(c -> c.getType() == ct).mapToInt(Camera::getMegapixels).min().orElse(0);
    }

    private long countPhonesAtMp(List<Phone> phones, CameraType ct, int target) {
        return phones.stream()
                .filter(p -> p.getCameras().stream()
                        .filter(c -> c.getType() == ct)
                        .mapToInt(Camera::getMegapixels)
                        .max().orElse(-1) == target)
                .count();
    }

    private void addSpecCompareData(List<Phone> phones, Model model) {
        int maxBattery = maxInt(phones, Phone::getBatteryCapacity);
        int minBattery = minInt(phones, Phone::getBatteryCapacity);
        double maxScreen = phones.stream().filter(p -> p.getScreenSize() != null)
                .mapToDouble(Phone::getScreenSize).max().orElse(0);
        double minScreen = phones.stream().filter(p -> p.getScreenSize() != null)
                .mapToDouble(Phone::getScreenSize).min().orElse(0);
        long maxPx = phones.stream().filter(p -> p.getScreenResolution() != null)
                .mapToLong(p -> p.getScreenResolution().getPixelCount()).max().orElse(0);
        long minPx = phones.stream().filter(p -> p.getScreenResolution() != null)
                .mapToLong(p -> p.getScreenResolution().getPixelCount()).min().orElse(0);
        int maxRam = maxInt(phones, Phone::getRamGb);
        int minRam = minInt(phones, Phone::getRamGb);
        int maxRom = maxInt(phones, Phone::getRomGb);
        int minRom = minInt(phones, Phone::getRomGb);
        int maxCharging = maxInt(phones, Phone::getChargingPower);
        int minCharging = minInt(phones, Phone::getChargingPower);

        model.addAttribute("maxBattery", maxBattery);
        model.addAttribute("maxBatteryCount", countIntAt(phones, Phone::getBatteryCapacity, maxBattery));
        model.addAttribute("minBattery", minBattery);
        model.addAttribute("minBatteryCount", countIntAt(phones, Phone::getBatteryCapacity, minBattery));
        model.addAttribute("maxScreenSize", maxScreen);
        model.addAttribute("maxScreenSizeCount", phones.stream().filter(p -> p.getScreenSize() != null && p.getScreenSize() == maxScreen).count());
        model.addAttribute("minScreenSize", minScreen);
        model.addAttribute("minScreenSizeCount", phones.stream().filter(p -> p.getScreenSize() != null && p.getScreenSize() == minScreen).count());
        model.addAttribute("maxPixelCount", maxPx);
        model.addAttribute("maxPixelCountCount", phones.stream().filter(p -> p.getScreenResolution() != null && p.getScreenResolution().getPixelCount() == maxPx).count());
        model.addAttribute("minPixelCount", minPx);
        model.addAttribute("minPixelCountCount", phones.stream().filter(p -> p.getScreenResolution() != null && p.getScreenResolution().getPixelCount() == minPx).count());
        model.addAttribute("maxRam", maxRam);
        model.addAttribute("maxRamCount", countIntAt(phones, Phone::getRamGb, maxRam));
        model.addAttribute("minRam", minRam);
        model.addAttribute("minRamCount", countIntAt(phones, Phone::getRamGb, minRam));
        model.addAttribute("maxRom", maxRom);
        model.addAttribute("maxRomCount", countIntAt(phones, Phone::getRomGb, maxRom));
        model.addAttribute("minRom", minRom);
        model.addAttribute("minRomCount", countIntAt(phones, Phone::getRomGb, minRom));
        model.addAttribute("maxCharging", maxCharging);
        model.addAttribute("maxChargingCount", countIntAt(phones, Phone::getChargingPower, maxCharging));
        model.addAttribute("minCharging", minCharging);
        model.addAttribute("minChargingCount", countIntAt(phones, Phone::getChargingPower, minCharging));
    }

    private int maxInt(List<Phone> phones, ToIntFunction<Phone> getter) {
        return phones.stream().mapToInt(getter).max().orElse(0);
    }

    private int minInt(List<Phone> phones, ToIntFunction<Phone> getter) {
        return phones.stream().mapToInt(getter).min().orElse(0);
    }

    private long countIntAt(List<Phone> phones, ToIntFunction<Phone> getter, int target) {
        return phones.stream()
                .filter(p -> getter.applyAsInt(p) == target)
                .count();
    }

    private List<Long> parseIds(String ids) {
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .toList();
    }
}
