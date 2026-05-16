package com.boyitong.controller;

import com.boyitong.common.PageResult;
import com.boyitong.common.Result;
import com.boyitong.dto.CustomerQueryDTO;
import com.boyitong.dto.CustomerVO;
import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import com.boyitong.service.AuditLogService;
import com.boyitong.service.CustomerService;
import com.boyitong.service.CustomerServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerServiceImpl customerServiceImpl;
    private final CustomerRepository customerRepository;
    private final AuditLogService auditLogService;

    public CustomerController(CustomerService customerService,
                              CustomerServiceImpl customerServiceImpl,
                              CustomerRepository customerRepository,
                              AuditLogService auditLogService) {
        this.customerService = customerService;
        this.customerServiceImpl = customerServiceImpl;
        this.customerRepository = customerRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public Result<PageResult<CustomerVO>> list(CustomerQueryDTO query) {
        String assignedTo = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                assignedTo = auth.getName();
            }
        }

        Page<Customer> page = customerServiceImpl.searchCustomers(
                query.getCity(), query.getArea(), query.getCategory(),
                query.getMinSize(), query.getMaxSize(),
                query.getSalesperson(), query.getKeyword(),
                assignedTo,
                query.getPage(), query.getSize(),
                query.getSortBy(), query.getSortDir());

        List<CustomerVO> voList = page.getContent().stream()
                .map(CustomerVO::fromEntity)
                .toList();

        PageResult<CustomerVO> pageResult = new PageResult<>(
                voList, page.getNumber(), page.getSize(), page.getTotalElements());

        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<CustomerVO> getById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        checkCustomerAccess(customer);
        return Result.success(CustomerVO.fromEntity(customer));
    }

    @PutMapping("/{id}")
    public Result<CustomerVO> update(@PathVariable Long id, @RequestBody CustomerVO vo) {
        Customer existing = customerService.findById(id);
        checkCustomerAccess(existing);
        if (vo.getCity() != null) existing.setCity(vo.getCity());
        if (vo.getArea() != null) existing.setArea(vo.getArea());
        if (vo.getAddress() != null) existing.setAddress(vo.getAddress());
        if (vo.getCategory() != null) existing.setCategory(vo.getCategory());
        if (vo.getSize() != null) existing.setSize(vo.getSize());
        if (vo.getPhone() != null) existing.setPhone(vo.getPhone());
        if (vo.getSalesperson() != null) existing.setSalesperson(vo.getSalesperson());
        if (vo.getRemarks() != null) existing.setRemarks(vo.getRemarks());
        if (vo.getDate() != null) existing.setDate(vo.getDate());
        if (vo.getExpiryDate() != null) existing.setExpiryDate(vo.getExpiryDate());
        customerServiceImpl.getCustomerRepository().save(existing);

        String username = getCurrentUsername();
        if (username != null) {
            auditLogService.log(username, "UPDATE", "Customer", String.valueOf(id),
                    "更新客户数据 #" + id);
        }

        return Result.success(CustomerVO.fromEntity(existing));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Customer customer = customerService.findById(id); // ensure exists
        checkCustomerAccess(customer);
        customerServiceImpl.getCustomerRepository().deleteById(id);

        String username = getCurrentUsername();
        if (username != null) {
            auditLogService.log(username, "DELETE", "Customer", String.valueOf(id),
                    "删除客户数据 #" + id);
        }

        return Result.success();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }
        return null;
    }

    /** 校验当前用户是否有权访问该客户 */
    private void checkCustomerAccess(Customer customer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                String username = auth.getName();
                if (customer.getAssignedTo() == null || !customer.getAssignedTo().equals(username)) {
                    throw new RuntimeException("无权访问该客户数据");
                }
            }
        }
    }

    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file,
                                      @RequestParam("city") String city) {
        List<Customer> customers = customerService.importExcel(file, city);
        return Result.success("Imported " + customers.size() + " customers from " + city);
    }

    /** 管理员分配客户给销售员 */
    @PutMapping("/{id}/assign")
    public Result<CustomerVO> assign(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Customer customer = customerService.findById(id);
        customer.setAssignedTo(body.get("assignedTo"));
        customerRepository.save(customer);

        String username = getCurrentUsername();
        if (username != null) {
            auditLogService.log(username, "ASSIGN", "Customer", String.valueOf(id),
                    "分配客户 #" + id + " 给 " + body.get("assignedTo"));
        }
        return Result.success(CustomerVO.fromEntity(customer));
    }

    /** 管理员变更客户状态 */
    @PutMapping("/{id}/status")
    public Result<CustomerVO> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Customer customer = customerService.findById(id);
        customer.setStatus(body.get("status"));
        customerRepository.save(customer);
        return Result.success(CustomerVO.fromEntity(customer));
    }

    /** 检查重复客户 */
    @GetMapping("/check-duplicate")
    public Result<List<CustomerVO>> checkDuplicate(@RequestParam(required = false) String phone,
                                                   @RequestParam(required = false) String address) {
        List<Customer> matches;
        if (phone != null && !phone.isBlank()) {
            matches = customerRepository.findByPhoneContaining(phone);
        } else if (address != null && !address.isBlank()) {
            matches = customerRepository.findByAddressContaining(address);
        } else {
            return Result.success(List.of());
        }
        return Result.success(matches.stream().map(CustomerVO::fromEntity).toList());
    }

    /** 客户下拉选项（轻量级，仅返回 id + 地址） */
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getOptions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Customer> all;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                all = customerRepository.findAll();
            } else {
                all = customerRepository.findByAssignedTo(auth.getName());
            }
        } else {
            all = customerRepository.findAll();
        }
        List<Map<String, Object>> options = all.stream().map(c -> {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", c.getId());
            m.put("label", "#" + c.getId() + " " + (c.getAddress() != null ? c.getAddress().substring(0, Math.min(c.getAddress().length(), 30)) : ""));
            return m;
        }).toList();
        return Result.success(options);
    }

    /** 获取未分配的客户 */
    @GetMapping("/unassigned")
    public Result<List<CustomerVO>> getUnassigned() {
        return Result.success(customerRepository.findByAssignedToIsNullOrAssignedTo("")
                .stream().map(CustomerVO::fromEntity).toList());
    }

    /** 获取分配给当前用户的客户 */
    @GetMapping("/my-customers")
    public Result<List<CustomerVO>> getMyCustomers() {
        String username = getCurrentUsername();
        if (username == null) return Result.success(List.of());
        return Result.success(customerRepository.findByAssignedTo(username)
                .stream().map(CustomerVO::fromEntity).toList());
    }
}