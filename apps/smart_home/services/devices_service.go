package services

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	"smarthome/models"
)

// DevicesService registers sensors in the devices microservice.
type DevicesService struct {
	BaseURL    string
	HTTPClient *http.Client
}

// NewDevicesService creates a client for the devices microservice.
func NewDevicesService(baseURL string) *DevicesService {
	return &DevicesService{
		BaseURL: baseURL,
		HTTPClient: &http.Client{
			Timeout: 5 * time.Second,
		},
	}
}

// Register sends a created sensor to the devices microservice.
func (s *DevicesService) Register(sensor models.Sensor) error {
	body, err := json.Marshal(sensor)
	if err != nil {
		return fmt.Errorf("encode device: %w", err)
	}

	resp, err := s.HTTPClient.Post(s.BaseURL+"/devices", "application/json", bytes.NewReader(body))
	if err != nil {
		return fmt.Errorf("call devices service: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated && resp.StatusCode != http.StatusOK {
		return fmt.Errorf("devices service returned status %d", resp.StatusCode)
	}
	return nil
}
