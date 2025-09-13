import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { JobSearchForm } from '../job-search-form'

describe('JobSearchForm', () => {
  const mockOnSearch = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('renders search form correctly', () => {
    render(<JobSearchForm onSearch={mockOnSearch} />)

    expect(screen.getByPlaceholderText(/search jobs/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/location/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/job type/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/salary range/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument()
  })

  test('submits search with keyword only', async () => {
    const user = userEvent.setup()
    render(<JobSearchForm onSearch={mockOnSearch} />)

    const searchInput = screen.getByPlaceholderText(/search jobs/i)
    const searchButton = screen.getByRole('button', { name: /search/i })

    await user.type(searchInput, 'software engineer')
    await user.click(searchButton)

    expect(mockOnSearch).toHaveBeenCalledWith({
      search: 'software engineer',
      location: '',
      jobType: '',
      salaryMin: '',
      salaryMax: '',
    })
  })

  test('submits search with all filters', async () => {
    const user = userEvent.setup()
    render(<JobSearchForm onSearch={mockOnSearch} />)

    const searchInput = screen.getByPlaceholderText(/search jobs/i)
    const locationInput = screen.getByLabelText(/location/i)
    const jobTypeSelect = screen.getByLabelText(/job type/i)
    const salaryMinInput = screen.getByLabelText(/minimum salary/i)
    const salaryMaxInput = screen.getByLabelText(/maximum salary/i)
    const searchButton = screen.getByRole('button', { name: /search/i })

    await user.type(searchInput, 'react developer')
    await user.type(locationInput, 'New York')
    await user.selectOptions(jobTypeSelect, 'FULL_TIME')
    await user.type(salaryMinInput, '80000')
    await user.type(salaryMaxInput, '120000')
    await user.click(searchButton)

    expect(mockOnSearch).toHaveBeenCalledWith({
      search: 'react developer',
      location: 'New York',
      jobType: 'FULL_TIME',
      salaryMin: '80000',
      salaryMax: '120000',
    })
  })

  test('clears all filters', async () => {
    const user = userEvent.setup()
    render(<JobSearchForm onSearch={mockOnSearch} />)

    const searchInput = screen.getByPlaceholderText(/search jobs/i)
    const locationInput = screen.getByLabelText(/location/i)
    const clearButton = screen.getByRole('button', { name: /clear filters/i })

    await user.type(searchInput, 'test')
    await user.type(locationInput, 'test location')
    await user.click(clearButton)

    expect(searchInput).toHaveValue('')
    expect(locationInput).toHaveValue('')
    expect(mockOnSearch).toHaveBeenCalledWith({
      search: '',
      location: '',
      jobType: '',
      salaryMin: '',
      salaryMax: '',
    })
  })

  test('validates salary range', async () => {
    const user = userEvent.setup()
    render(<JobSearchForm onSearch={mockOnSearch} />)

    const salaryMinInput = screen.getByLabelText(/minimum salary/i)
    const salaryMaxInput = screen.getByLabelText(/maximum salary/i)
    const searchButton = screen.getByRole('button', { name: /search/i })

    await user.type(salaryMinInput, '120000')
    await user.type(salaryMaxInput, '80000')
    await user.click(searchButton)

    await waitFor(() => {
      expect(screen.getByText(/minimum salary cannot be greater than maximum salary/i)).toBeInTheDocument()
    })

    expect(mockOnSearch).not.toHaveBeenCalled()
  })

  test('shows recent searches', () => {
    const recentSearches = ['software engineer', 'react developer', 'python developer']
    
    render(<JobSearchForm onSearch={mockOnSearch} recentSearches={recentSearches} />)

    expect(screen.getByText(/recent searches/i)).toBeInTheDocument()
    recentSearches.forEach(search => {
      expect(screen.getByText(search)).toBeInTheDocument()
    })
  })

  test('applies recent search when clicked', async () => {
    const user = userEvent.setup()
    const recentSearches = ['software engineer']
    
    render(<JobSearchForm onSearch={mockOnSearch} recentSearches={recentSearches} />)

    const recentSearchItem = screen.getByText('software engineer')
    await user.click(recentSearchItem)

    expect(mockOnSearch).toHaveBeenCalledWith({
      search: 'software engineer',
      location: '',
      jobType: '',
      salaryMin: '',
      salaryMax: '',
    })
  })

  test('shows popular job types', () => {
    render(<JobSearchForm onSearch={mockOnSearch} />)

    expect(screen.getByText('Full Time')).toBeInTheDocument()
    expect(screen.getByText('Part Time')).toBeInTheDocument()
    expect(screen.getByText('Contract')).toBeInTheDocument()
    expect(screen.getByText('Remote')).toBeInTheDocument()
  })

  test('applies job type filter when popular type is clicked', async () => {
    const user = userEvent.setup()
    render(<JobSearchForm onSearch={mockOnSearch} />)

    const remoteButton = screen.getByText('Remote')
    await user.click(remoteButton)

    expect(mockOnSearch).toHaveBeenCalledWith({
      search: '',
      location: '',
      jobType: 'REMOTE',
      salaryMin: '',
      salaryMax: '',
    })
  })
})